package com.example.sbam.services

import com.example.sbam.dtos.CategorySummary
import com.example.sbam.dtos.MonthlyBreakdown
import com.example.sbam.dtos.MonthlyReportResponse
import com.example.sbam.dtos.YearlyReportResponse
import com.example.sbam.models.CategoryType
import com.example.sbam.repositories.TransactionRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.time.LocalDate

@Service
class ReportService(
    private val transactionRepository: TransactionRepository,
    private val categoryService: CategoryService
) {

    fun getMonthlyReport(year: Int, month: Int): MonthlyReportResponse {
        val user = categoryService.getAuthenticatedUser()

        val startDate = LocalDate.of(year, month, 1)
        val endDate = startDate.plusMonths(1).minusDays(1)

        val transactions = transactionRepository.findFilteredTransactions(user, startDate, endDate, null, null)

        val incomeByCategoryMap = transactions.filter { it.category.type == CategoryType.INCOME }
            .groupBy { it.category.name }
        val expenseByCategoryMap = transactions.filter { it.category.type == CategoryType.EXPENSE }
            .groupBy { it.category.name }

        val incomeByCategory = incomeByCategoryMap.map { (name, txs) ->
            CategorySummary(name, txs.fold(BigDecimal.ZERO) { acc, tx -> acc.add(tx.amount) })
        }

        val expenseByCategory = expenseByCategoryMap.map { (name, txs) ->
            CategorySummary(name, txs.fold(BigDecimal.ZERO) { acc, tx -> acc.add(tx.amount) })
        }

        val totalIncome = incomeByCategory.fold(BigDecimal.ZERO) { acc, cs -> acc.add(cs.totalAmount) }
        val totalExpenses = expenseByCategory.fold(BigDecimal.ZERO) { acc, cs -> acc.add(cs.totalAmount) }
        val netSavings = totalIncome.subtract(totalExpenses)

        return MonthlyReportResponse(
            year = year,
            month = month,
            totalIncome = totalIncome,
            totalExpenses = totalExpenses,
            netSavings = netSavings,
            incomeByCategory = incomeByCategory,
            expenseByCategory = expenseByCategory
        )
    }

    fun getYearlyReport(year: Int): YearlyReportResponse {
        val user = categoryService.getAuthenticatedUser()

        val startDate = LocalDate.of(year, 1, 1)
        val endDate = LocalDate.of(year, 12, 31)

        val transactions = transactionRepository.findFilteredTransactions(user, startDate, endDate, null, null)

        val incomeByCategoryMap = transactions.filter { it.category.type == CategoryType.INCOME }
            .groupBy { it.category.name }
        val expenseByCategoryMap = transactions.filter { it.category.type == CategoryType.EXPENSE }
            .groupBy { it.category.name }

        val incomeByCategory = incomeByCategoryMap.map { (name, txs) ->
            CategorySummary(name, txs.fold(BigDecimal.ZERO) { acc, tx -> acc.add(tx.amount) })
        }

        val expenseByCategory = expenseByCategoryMap.map { (name, txs) ->
            CategorySummary(name, txs.fold(BigDecimal.ZERO) { acc, tx -> acc.add(tx.amount) })
        }

        val totalIncome = incomeByCategory.fold(BigDecimal.ZERO) { acc, cs -> acc.add(cs.totalAmount) }
        val totalExpenses = expenseByCategory.fold(BigDecimal.ZERO) { acc, cs -> acc.add(cs.totalAmount) }
        val netSavings = totalIncome.subtract(totalExpenses)

        val monthlyBreakdown = (1..12).map { m ->
            val monthTxs = transactions.filter { it.date.monthValue == m }
            val inc = monthTxs.filter { it.category.type == CategoryType.INCOME }
                .fold(BigDecimal.ZERO) { acc, tx -> acc.add(tx.amount) }
            val exp = monthTxs.filter { it.category.type == CategoryType.EXPENSE }
                .fold(BigDecimal.ZERO) { acc, tx -> acc.add(tx.amount) }
            MonthlyBreakdown(m, inc, exp, inc.subtract(exp))
        }

        return YearlyReportResponse(
            year = year,
            totalIncome = totalIncome,
            totalExpenses = totalExpenses,
            netSavings = netSavings,
            monthlyBreakdown = monthlyBreakdown,
            incomeByCategory = incomeByCategory,
            expenseByCategory = expenseByCategory
        )
    }
}
