package com.example.sbam.dtos

import java.math.BigDecimal

data class CategorySummary(
    val categoryName: String,
    val totalAmount: BigDecimal
)

data class MonthlyBreakdown(
    val month: Int,
    val totalIncome: BigDecimal,
    val totalExpenses: BigDecimal,
    val netSavings: BigDecimal
)

data class MonthlyReportResponse(
    val year: Int,
    val month: Int,
    val totalIncome: BigDecimal,
    val totalExpenses: BigDecimal,
    val netSavings: BigDecimal,
    val incomeByCategory: List<CategorySummary>,
    val expenseByCategory: List<CategorySummary>
)

data class YearlyReportResponse(
    val year: Int,
    val totalIncome: BigDecimal,
    val totalExpenses: BigDecimal,
    val netSavings: BigDecimal,
    val monthlyBreakdown: List<MonthlyBreakdown>,
    val incomeByCategory: List<CategorySummary>,
    val expenseByCategory: List<CategorySummary>
)
