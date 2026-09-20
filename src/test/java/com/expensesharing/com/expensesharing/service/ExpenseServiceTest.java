package com.expensesharing.com.expensesharing.service;

import com.expensesharing.com.expensesharing.entity.Expense;
import com.expensesharing.com.expensesharing.entity.Participant;
import com.expensesharing.com.expensesharing.entity.SplitType;
import com.expensesharing.com.expensesharing.entity.User;
import com.expensesharing.com.expensesharing.repositories.ExpenseRepository;
import com.expensesharing.com.expensesharing.repositories.UserRepository;
import com.expensesharing.com.expensesharing.util.ExpenseSplitUtil;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ExpenseServiceTest {

    @InjectMocks
    private ExpenseService expenseService;

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExpenseSplitUtil expenseSplitUtil;

    @Mock
    private com.expensesharing.com.expensesharing.util.DebtSimplificationUtil debtSimplificationUtil;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddExpense_Success() throws Exception {
        Expense expense = new Expense();
        Participant participant = new Participant(1L, 100.0, 0.0);
        expense.setParticipants(Arrays.asList(participant));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);

        Expense result = expenseService.addExpense(expense);
        assertEquals(expense, result);
        verify(expenseSplitUtil, times(1)).splitExpense(expense);
        verify(expenseRepository, times(1)).save(expense);
    }

    @Test
    public void testAddExpense_UserNotFound() {
        Expense expense = new Expense();
        Participant participant = new Participant(1L, 100.0, 0.0);
        expense.setParticipants(Arrays.asList(participant));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ValidationException.class, () -> {
            expenseService.addExpense(expense);
        });

        String expectedMessage = "User not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
        verify(expenseRepository, times(0)).save(any(Expense.class));
    }

    @Test
    public void testAddExpense_ExactAmountsMismatch_ShouldThrow() {
        Expense expense = new Expense();
        expense.setSplitType(SplitType.EXACT);
        expense.setTotalAmount(1000.0);
        Participant p1 = new Participant(1L, 400.0, null);
        Participant p2 = new Participant(2L, 300.0, null);
        expense.setParticipants(Arrays.asList(p1, p2));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            expenseService.addExpense(expense);
        });

        assertTrue(exception.getMessage().contains("must equal the total amount"));
        verify(expenseRepository, times(0)).save(any(Expense.class));
    }

    @Test
    public void testAddExpense_ExactAmountsMatch_ShouldSucceed() throws Exception {
        Expense expense = new Expense();
        expense.setSplitType(SplitType.EXACT);
        expense.setTotalAmount(1000.0);
        Participant p1 = new Participant(1L, 600.0, null);
        Participant p2 = new Participant(2L, 400.0, null);
        expense.setParticipants(Arrays.asList(p1, p2));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));
        when(expenseRepository.save(any(Expense.class))).thenReturn(expense);

        Expense result = expenseService.addExpense(expense);

        assertEquals(expense, result);
        verify(expenseRepository, times(1)).save(expense);
    }

    @Test
    public void testAddExpense_PercentagesNotHundred_ShouldThrow() {
        Expense expense = new Expense();
        expense.setSplitType(SplitType.PERCENTAGE);
        expense.setTotalAmount(1000.0);
        Participant p1 = new Participant(1L, null, 40.0);
        Participant p2 = new Participant(2L, null, 40.0);
        expense.setParticipants(Arrays.asList(p1, p2));
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
        when(userRepository.findById(2L)).thenReturn(Optional.of(new User()));

        ValidationException exception = assertThrows(ValidationException.class, () -> {
            expenseService.addExpense(expense);
        });

        assertTrue(exception.getMessage().contains("must equal 100"));
        verify(expenseRepository, times(0)).save(any(Expense.class));
    }

    @Test
    public void testGetExpenseById_Success() {
        Expense expense = new Expense();
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(expense));

        Expense result = expenseService.getExpenseById(1L);
        assertEquals(expense, result);
    }

    @Test
    public void testGetExpenseById_NotFound() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ValidationException.class, () -> {
            expenseService.getExpenseById(1L);
        });

        String expectedMessage = "Expense not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testGetAllExpenses() {
        List<Expense> expenses = Arrays.asList(new Expense(), new Expense());
        when(expenseRepository.findAll()).thenReturn(expenses);

        List<Expense> result = expenseService.getAllExpenses();
        assertEquals(expenses, result);
    }

    @Test
    public void testGetExpensesByUserId() {
        List<Expense> expenses = Arrays.asList(new Expense(), new Expense());
        when(expenseRepository.findByParticipantsUserId(1L)).thenReturn(expenses);

        List<Expense> result = expenseService.getExpensesByUserId(1L);
        assertEquals(expenses, result);
    }

    @Test
    public void testGenerateBalanceSheetData_Success() {
        Expense expense = new Expense();
        Participant participant = new Participant(1L, 100.0, 0.0);
        expense.setParticipants(Arrays.asList(participant));
        when(expenseRepository.findAll()).thenReturn(Arrays.asList(expense));
        User user = new User();
        user.setName("John Doe");
        user.setEmail("john.doe@example.com");
        user.setMobile("1234567890");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        List<Map<String, Object>> result = expenseService.generateBalanceSheetData();
        assertEquals(1, result.size());
        Map<String, Object> balanceEntry = result.get(0);
        assertEquals("John Doe", balanceEntry.get("userName"));
        assertEquals("john.doe@example.com", balanceEntry.get("email"));
        assertEquals("1234567890", balanceEntry.get("mobile"));
        assertEquals(100.0, balanceEntry.get("amount"));
    }

    @Test
    public void testGenerateBalanceSheetData_UserNotFound() {
        Expense expense = new Expense();
        Participant participant = new Participant(1L, 100.0, 0.0);
        expense.setParticipants(Arrays.asList(participant));
        when(expenseRepository.findAll()).thenReturn(Arrays.asList(expense));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        Exception exception = assertThrows(ValidationException.class, () -> {
            expenseService.generateBalanceSheetData();
        });

        String expectedMessage = "User not found";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void testComputeNetBalances_CreditsPayerAndDebitsParticipants() {
        Expense expense = new Expense();
        expense.setTotalAmount(100.0);
        expense.setPaidByUserId(1L);
        Participant p1 = new Participant(1L, 50.0, null);
        Participant p2 = new Participant(2L, 50.0, null);
        expense.setParticipants(Arrays.asList(p1, p2));
        when(expenseRepository.findAll()).thenReturn(Arrays.asList(expense));

        Map<Long, Double> net = expenseService.computeNetBalances();

        // Payer (user 1) paid 100 but owes 50 -> net +50; user 2 owes 50 -> net -50
        assertEquals(50.0, net.get(1L), 0.001);
        assertEquals(-50.0, net.get(2L), 0.001);
    }

    @Test
    public void testComputeNetBalances_IgnoresExpensesWithoutPayer() {
        Expense expense = new Expense();
        expense.setTotalAmount(100.0);
        expense.setPaidByUserId(null);
        Participant p1 = new Participant(1L, 100.0, null);
        expense.setParticipants(Arrays.asList(p1));
        when(expenseRepository.findAll()).thenReturn(Arrays.asList(expense));

        Map<Long, Double> net = expenseService.computeNetBalances();

        assertTrue(net.isEmpty());
    }

    @Test
    public void testComputeAnalytics_AggregatesTotalsAndBreakdowns() {
        Expense e1 = new Expense();
        e1.setDescription("Dinner");
        e1.setTotalAmount(300.0);
        e1.setSplitType(SplitType.EQUAL);
        e1.setPaidByUserId(1L);
        e1.setParticipants(Arrays.asList(
                new Participant(1L, 150.0, null),
                new Participant(2L, 150.0, null)));

        Expense e2 = new Expense();
        e2.setDescription("Cab");
        e2.setTotalAmount(100.0);
        e2.setSplitType(SplitType.EQUAL);
        e2.setPaidByUserId(2L);
        e2.setParticipants(Arrays.asList(
                new Participant(1L, 50.0, null),
                new Participant(2L, 50.0, null)));

        when(expenseRepository.findAll()).thenReturn(Arrays.asList(e1, e2));
        User alice = new User();
        alice.setName("Alice");
        User bob = new User();
        bob.setName("Bob");
        when(userRepository.findById(1L)).thenReturn(Optional.of(alice));
        when(userRepository.findById(2L)).thenReturn(Optional.of(bob));

        com.expensesharing.com.expensesharing.dto.ExpenseAnalytics analytics =
                expenseService.computeAnalytics();

        assertEquals(2, analytics.getTotalExpenses());
        assertEquals(400.0, analytics.getTotalAmount(), 0.001);
        assertEquals(200.0, analytics.getAverageExpenseAmount(), 0.001);
        assertEquals(2L, analytics.getCountBySplitType().get("EQUAL"));
        assertEquals(200.0, analytics.getTotalOwedByUser().get("Alice"), 0.001);
        assertEquals(200.0, analytics.getTotalOwedByUser().get("Bob"), 0.001);
        assertEquals(300.0, analytics.getTotalPaidByUser().get("Alice"), 0.001);
        assertEquals(100.0, analytics.getTotalPaidByUser().get("Bob"), 0.001);
    }

    @Test
    public void testComputeAnalytics_EmptyWhenNoExpenses() {
        when(expenseRepository.findAll()).thenReturn(Arrays.asList());

        com.expensesharing.com.expensesharing.dto.ExpenseAnalytics analytics =
                expenseService.computeAnalytics();

        assertEquals(0, analytics.getTotalExpenses());
        assertEquals(0.0, analytics.getTotalAmount(), 0.001);
        assertEquals(0.0, analytics.getAverageExpenseAmount(), 0.001);
        assertTrue(analytics.getCountBySplitType().isEmpty());
    }
}
