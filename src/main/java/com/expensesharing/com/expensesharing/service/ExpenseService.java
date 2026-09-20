package com.expensesharing.com.expensesharing.service;

import com.expensesharing.com.expensesharing.dto.ExpenseAnalytics;
import com.expensesharing.com.expensesharing.dto.Settlement;
import com.expensesharing.com.expensesharing.entity.Expense;
import com.expensesharing.com.expensesharing.entity.Participant;
import com.expensesharing.com.expensesharing.entity.SplitType;
import com.expensesharing.com.expensesharing.entity.User;
import com.expensesharing.com.expensesharing.repositories.ExpenseRepository;
import com.expensesharing.com.expensesharing.repositories.UserRepository;
import com.expensesharing.com.expensesharing.util.DebtSimplificationUtil;
import com.expensesharing.com.expensesharing.util.ExpenseSplitUtil;
import jakarta.validation.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseSplitUtil expenseSplitUtil;

    @Autowired
    private DebtSimplificationUtil debtSimplificationUtil;

    public Expense addExpense(Expense expense) {
        validateParticipants(expense.getParticipants());
        validateSplit(expense);
        try {
            expenseSplitUtil.splitExpense(expense);
            return expenseRepository.save(expense);
        } catch (Exception e) {
            throw new RuntimeException("Failed to add expense", e);
        }
    }

    public Expense getExpenseById(Long id) {
        return expenseRepository.findById(id).orElseThrow(() -> new ValidationException("Expense not found"));
    }

    public List<Expense> getAllExpenses() {
        return expenseRepository.findAll();
    }

    public List<Expense> getExpensesByUserId(Long userId) {
        return expenseRepository.findByParticipantsUserId(userId);
    }

    private void validateParticipants(List<Participant> participants) {
        if (participants == null || participants.isEmpty()) {
            throw new ValidationException("At least one participant is required");
        }
        for (Participant participant : participants) {
            userRepository.findById(participant.getUserId())
                    .orElseThrow(() -> new ValidationException("User not found"));
        }
    }

    // Ensures the chosen split strategy is internally consistent before persisting:
    //  - EXACT      : the participants' amounts must add up to the expense total
    //  - PERCENTAGE : the participants' percentages must add up to 100
    private void validateSplit(Expense expense) {
        SplitType splitType = expense.getSplitType();
        if (splitType == null) {
            return;
        }
        List<Participant> participants = expense.getParticipants();
        double tolerance = 0.01;

        switch (splitType) {
            case EXACT:
                double amountSum = participants.stream()
                        .mapToDouble(p -> p.getAmount() == null ? 0.0 : p.getAmount())
                        .sum();
                if (expense.getTotalAmount() == null
                        || Math.abs(amountSum - expense.getTotalAmount()) > tolerance) {
                    throw new ValidationException(
                            "Sum of exact amounts (" + amountSum + ") must equal the total amount ("
                                    + expense.getTotalAmount() + ")");
                }
                break;
            case PERCENTAGE:
                double percentageSum = participants.stream()
                        .mapToDouble(p -> p.getPercentage() == null ? 0.0 : p.getPercentage())
                        .sum();
                if (Math.abs(percentageSum - 100.0) > tolerance) {
                    throw new ValidationException(
                            "Sum of percentages (" + percentageSum + ") must equal 100");
                }
                break;
            default:
                break;
        }
    }


    // This is the method which return the data in json format where it will be easy for the frontend users to display it in UI..
    public List<Map<String, Object>> generateBalanceSheetData() {
        List<Expense> allExpenses = getAllExpenses();
        List<Map<String, Object>> balanceSheetData = new ArrayList<>();

        for (Expense expense : allExpenses) {
            for (Participant participant : expense.getParticipants()) {
                User user = userRepository.findById(participant.getUserId())
                        .orElseThrow(() -> new ValidationException("User not found"));

                Map<String, Object> balanceEntry = new HashMap<>();
                balanceEntry.put("userName", user.getName());
                balanceEntry.put("email", user.getEmail());
                balanceEntry.put("mobile", user.getMobile());
                balanceEntry.put("expenseDescription", expense.getDescription());
                balanceEntry.put("amount", participant.getAmount());

                balanceSheetData.add(balanceEntry);
            }
        }

        return balanceSheetData;
    }

    // Computes each user's net position across every expense that records a payer:
    //   the payer is credited the full amount, and every participant is debited their share.
    // Positive net = the user is owed money; negative net = the user owes money.
    public Map<Long, Double> computeNetBalances() {
        return computeNetBalances(getAllExpenses());
    }

    public Map<Long, Double> computeNetBalances(List<Expense> expenses) {
        Map<Long, Double> netByUser = new HashMap<>();

        for (Expense expense : expenses) {
            Long payerId = expense.getPaidByUserId();
            if (payerId == null || expense.getParticipants() == null) {
                continue;
            }
            netByUser.merge(payerId, expense.getTotalAmount(), Double::sum);
            for (Participant participant : expense.getParticipants()) {
                double share = participant.getAmount() == null ? 0.0 : participant.getAmount();
                netByUser.merge(participant.getUserId(), -share, Double::sum);
            }
        }

        return netByUser;
    }

    // Simplifies all outstanding debts into the minimal set of "who pays whom" transfers.
    public List<Settlement> getSettlements() {
        return simplify(computeNetBalances());
    }

    // Simplifies debts for a specific set of expenses (e.g. a single group).
    public List<Settlement> getSettlementsFor(List<Expense> expenses) {
        return simplify(computeNetBalances(expenses));
    }

    private List<Settlement> simplify(Map<Long, Double> netByUser) {
        List<DebtSimplificationUtil.Transfer> transfers = debtSimplificationUtil.simplify(netByUser);

        List<Settlement> settlements = new ArrayList<>();
        for (DebtSimplificationUtil.Transfer transfer : transfers) {
            settlements.add(new Settlement(
                    transfer.getFromUserId(),
                    lookupUserName(transfer.getFromUserId()),
                    transfer.getToUserId(),
                    lookupUserName(transfer.getToUserId()),
                    transfer.getAmount()));
        }
        return settlements;
    }

    private String lookupUserName(Long userId) {
        return userRepository.findById(userId)
                .map(User::getName)
                .orElse("Unknown");
    }

    // Aggregates spending insights across every recorded expense:
    //   totals, average, per-split-type counts, and per-user owed/paid sums.
    public ExpenseAnalytics computeAnalytics() {
        List<Expense> expenses = getAllExpenses();

        long totalExpenses = expenses.size();
        double totalAmount = 0.0;
        Map<String, Long> countBySplitType = new HashMap<>();
        Map<String, Double> totalOwedByUser = new HashMap<>();
        Map<String, Double> totalPaidByUser = new HashMap<>();

        for (Expense expense : expenses) {
            if (expense.getTotalAmount() != null) {
                totalAmount += expense.getTotalAmount();
            }

            String splitType = expense.getSplitType() == null
                    ? "UNSPECIFIED" : expense.getSplitType().name();
            countBySplitType.merge(splitType, 1L, Long::sum);

            if (expense.getPaidByUserId() != null && expense.getTotalAmount() != null) {
                totalPaidByUser.merge(
                        lookupUserName(expense.getPaidByUserId()),
                        expense.getTotalAmount(), Double::sum);
            }

            if (expense.getParticipants() != null) {
                for (Participant participant : expense.getParticipants()) {
                    double share = participant.getAmount() == null ? 0.0 : participant.getAmount();
                    totalOwedByUser.merge(
                            lookupUserName(participant.getUserId()), share, Double::sum);
                }
            }
        }

        ExpenseAnalytics analytics = new ExpenseAnalytics();
        analytics.setTotalExpenses(totalExpenses);
        analytics.setTotalAmount(round(totalAmount));
        analytics.setAverageExpenseAmount(
                totalExpenses == 0 ? 0.0 : round(totalAmount / totalExpenses));
        analytics.setCountBySplitType(countBySplitType);
        analytics.setTotalOwedByUser(roundValues(totalOwedByUser));
        analytics.setTotalPaidByUser(roundValues(totalPaidByUser));
        return analytics;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private Map<String, Double> roundValues(Map<String, Double> map) {
        Map<String, Double> rounded = new HashMap<>();
        for (Map.Entry<String, Double> entry : map.entrySet()) {
            rounded.put(entry.getKey(), round(entry.getValue()));
        }
        return rounded;
    }


    // This is the method which is used to generate the data and save in the balance sheet...
//    public String generateAndSaveBalanceSheet() {
//        List<Expense> allExpenses = getAllExpenses();
//        StringBuilder balanceSheet = new StringBuilder();
//
//        balanceSheet.append("User, Email, Mobile, Expense Description, Amount\n");
//
//        for (Expense expense : allExpenses) {
//            for (Participant participant : expense.getParticipants()) {
//                User user = userRepository.findById(participant.getUserId())
//                        .orElseThrow(() -> new ValidationException("User not found"));
//                balanceSheet.append(user.getName()).append(", ")
//                        .append(user.getEmail()).append(", ")
//                        .append(user.getMobile()).append(", ")
//                        .append(expense.getDescription()).append(", ")
//                        .append(participant.getAmount()).append("\n");
//            }
//        }
//
//        String filePath = "src/main/resources/balance-sheet.csv";
//
//        try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File(filePath)))) {
//            writer.write(balanceSheet.toString());
//        } catch (IOException e) {
//            e.printStackTrace();
//            throw new RuntimeException("Failed to write balance sheet to file");
//        }
//
//        return filePath;
//    }



    // This is just for returning the data to the certain api as normal way


//    public String generateBalanceSheet() {
//        List<Expense> allExpenses = getAllExpenses();
//        StringBuilder balanceSheet = new StringBuilder();
//
//        balanceSheet.append("User, Email, Mobile, Expense Description, Amount\n");
//
//        for (Expense expense : allExpenses) {
//            for (Participant participant : expense.getParticipants()) {
//                User user = userRepository.findById(participant.getUserId())
//                        .orElseThrow(() -> new ValidationException("User not found"));
//                balanceSheet.append(user.getName()).append(", ")
//                        .append(user.getEmail()).append(", ")
//                        .append(user.getMobile()).append(", ")
//                        .append(expense.getDescription()).append(", ")
//                        .append(participant.getAmount()).append("\n");
//            }
//        }
//        return balanceSheet.toString();
//    }
}
