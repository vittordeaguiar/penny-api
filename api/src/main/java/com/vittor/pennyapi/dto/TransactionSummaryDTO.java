package com.vittor.pennyapi.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionSummaryDTO(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal balance,
        LocalDate startDate,
        LocalDate endDate
) {
    public TransactionSummaryDTO(BigDecimal totalIncome, BigDecimal totalExpense, LocalDate startDate, LocalDate endDate) {
        this(
                totalIncome != null ? totalIncome : BigDecimal.ZERO,
                totalExpense != null ? totalExpense : BigDecimal.ZERO,
                calculateBalance(totalIncome, totalExpense),
                startDate,
                endDate
        );
    }

    private static BigDecimal calculateBalance(BigDecimal totalIncome, BigDecimal totalExpense) {
        BigDecimal income = totalIncome != null ? totalIncome : BigDecimal.ZERO;
        BigDecimal expense = totalExpense != null ? totalExpense : BigDecimal.ZERO;
        return income.subtract(expense);
    }
}
