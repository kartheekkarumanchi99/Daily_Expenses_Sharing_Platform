package com.expensesharing.com.expensesharing.dto;

import java.util.Map;

public class ExpenseAnalytics {

    private long totalExpenses;
    private double totalAmount;
    private double averageExpenseAmount;
    private Map<String, Long> countBySplitType;
    private Map<String, Double> totalOwedByUser;
    private Map<String, Double> totalPaidByUser;

    public ExpenseAnalytics() {
    }

    public long getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(long totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getAverageExpenseAmount() {
        return averageExpenseAmount;
    }

    public void setAverageExpenseAmount(double averageExpenseAmount) {
        this.averageExpenseAmount = averageExpenseAmount;
    }

    public Map<String, Long> getCountBySplitType() {
        return countBySplitType;
    }

    public void setCountBySplitType(Map<String, Long> countBySplitType) {
        this.countBySplitType = countBySplitType;
    }

    public Map<String, Double> getTotalOwedByUser() {
        return totalOwedByUser;
    }

    public void setTotalOwedByUser(Map<String, Double> totalOwedByUser) {
        this.totalOwedByUser = totalOwedByUser;
    }

    public Map<String, Double> getTotalPaidByUser() {
        return totalPaidByUser;
    }

    public void setTotalPaidByUser(Map<String, Double> totalPaidByUser) {
        this.totalPaidByUser = totalPaidByUser;
    }
}
