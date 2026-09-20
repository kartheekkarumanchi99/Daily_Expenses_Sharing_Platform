package com.expensesharing.com.expensesharing.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Reduces a set of net balances into the minimum number of settlement transfers
 * using a greedy min-cash-flow approach: repeatedly settle the largest debtor
 * against the largest creditor.
 */
@Component
public class DebtSimplificationUtil {

    /** A single "from pays to" money transfer produced by the algorithm. */
    public static class Transfer {
        private final Long fromUserId;
        private final Long toUserId;
        private final double amount;

        public Transfer(Long fromUserId, Long toUserId, double amount) {
            this.fromUserId = fromUserId;
            this.toUserId = toUserId;
            this.amount = amount;
        }

        public Long getFromUserId() {
            return fromUserId;
        }

        public Long getToUserId() {
            return toUserId;
        }

        public double getAmount() {
            return amount;
        }
    }

    private static class Balance {
        final Long userId;
        double amount;

        Balance(Long userId, double amount) {
            this.userId = userId;
            this.amount = amount;
        }
    }

    private static final double EPSILON = 0.01;

    /**
     * @param netByUser positive value = the user is owed money (creditor),
     *                  negative value = the user owes money (debtor)
     * @return the minimal list of transfers that settles everyone
     */
    public List<Transfer> simplify(Map<Long, Double> netByUser) {
        PriorityQueue<Balance> creditors =
                new PriorityQueue<>(Comparator.comparingDouble(b -> -b.amount));
        PriorityQueue<Balance> debtors =
                new PriorityQueue<>(Comparator.comparingDouble(b -> b.amount));

        for (Map.Entry<Long, Double> entry : netByUser.entrySet()) {
            double value = round(entry.getValue());
            if (value > EPSILON) {
                creditors.add(new Balance(entry.getKey(), value));
            } else if (value < -EPSILON) {
                debtors.add(new Balance(entry.getKey(), value));
            }
        }

        List<Transfer> transfers = new ArrayList<>();

        while (!creditors.isEmpty() && !debtors.isEmpty()) {
            Balance creditor = creditors.poll();
            Balance debtor = debtors.poll();

            double settled = Math.min(creditor.amount, -debtor.amount);
            settled = round(settled);

            if (settled > EPSILON) {
                transfers.add(new Transfer(debtor.userId, creditor.userId, settled));
            }

            creditor.amount = round(creditor.amount - settled);
            debtor.amount = round(debtor.amount + settled);

            if (creditor.amount > EPSILON) {
                creditors.add(creditor);
            }
            if (debtor.amount < -EPSILON) {
                debtors.add(debtor);
            }
        }

        return transfers;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
