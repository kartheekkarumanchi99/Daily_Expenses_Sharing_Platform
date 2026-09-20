package com.expensesharing.com.expensesharing.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DebtSimplificationUtilTest {

    private DebtSimplificationUtil util;

    @BeforeEach
    void setUp() {
        util = new DebtSimplificationUtil();
    }

    @Test
    void emptyBalancesProduceNoTransfers() {
        assertTrue(util.simplify(new HashMap<>()).isEmpty());
    }

    @Test
    void singleDebtorSingleCreditorSettlesDirectly() {
        Map<Long, Double> net = new HashMap<>();
        net.put(1L, -100.0); // user 1 owes 100
        net.put(2L, 100.0);  // user 2 is owed 100

        List<DebtSimplificationUtil.Transfer> transfers = util.simplify(net);

        assertEquals(1, transfers.size());
        DebtSimplificationUtil.Transfer t = transfers.get(0);
        assertEquals(1L, t.getFromUserId());
        assertEquals(2L, t.getToUserId());
        assertEquals(100.0, t.getAmount(), 0.001);
    }

    @Test
    void minimizesNumberOfTransfers() {
        // A owes 30, B owes 20, C is owed 50 -> should be exactly 2 transfers.
        Map<Long, Double> net = new HashMap<>();
        net.put(1L, -30.0);
        net.put(2L, -20.0);
        net.put(3L, 50.0);

        List<DebtSimplificationUtil.Transfer> transfers = util.simplify(net);

        assertEquals(2, transfers.size());
        double totalToCreditor = transfers.stream()
                .filter(t -> t.getToUserId().equals(3L))
                .mapToDouble(DebtSimplificationUtil.Transfer::getAmount)
                .sum();
        assertEquals(50.0, totalToCreditor, 0.001);
    }

    @Test
    void conservationOfMoneyHolds() {
        Map<Long, Double> net = new HashMap<>();
        net.put(1L, -70.0);
        net.put(2L, -30.0);
        net.put(3L, 60.0);
        net.put(4L, 40.0);

        List<DebtSimplificationUtil.Transfer> transfers = util.simplify(net);

        double totalMoved = transfers.stream()
                .mapToDouble(DebtSimplificationUtil.Transfer::getAmount)
                .sum();
        assertEquals(100.0, totalMoved, 0.001);
    }
}
