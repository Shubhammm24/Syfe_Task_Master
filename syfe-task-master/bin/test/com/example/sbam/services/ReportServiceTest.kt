package com.example.sbam.services

import com.example.sbam.dtos.MonthlyReportResponse
import com.example.sbam.models.Category
import com.example.sbam.models.CategoryType
import com.example.sbam.models.Transaction
import com.example.sbam.models.User
import com.example.sbam.repositories.TransactionRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.math.BigDecimal
import java.time.LocalDate

class ReportServiceTest {

    private val transactionRepository = mock(TransactionRepository::class.java)
    private val categoryService = mock(CategoryService::class.java)
    private val reportService = ReportService(transactionRepository, categoryService)

    private val user = User(1L, "user@email.com", "hashed", "User Name", "1234")
    private val salary = Category(1L, "Salary", CategoryType.INCOME, false, null)
    private val food = Category(2L, "Food", CategoryType.EXPENSE, false, null)

    @BeforeEach
    fun setup() {
        `when`(categoryService.getAuthenticatedUser()).thenReturn(user)
    }

    @Test
    fun testGetMonthlyReport() {
        val tx1 = Transaction(1L, BigDecimal.valueOf(2000), LocalDate.of(2023, 5, 10), "Salary", salary, user)
        val tx2 = Transaction(2L, BigDecimal.valueOf(500), LocalDate.of(2023, 5, 15), "Groceries", food, user)

        val start = LocalDate.of(2023, 5, 1)
        val end = LocalDate.of(2023, 5, 31)

        `when`(transactionRepository.findFilteredTransactions(user, start, end, null, null))
            .thenReturn(listOf(tx1, tx2))

        val report = reportService.getMonthlyReport(2023, 5)

        assertEquals(2023, report.year)
        assertEquals(5, report.month)
        assertEquals(BigDecimal.valueOf(2000), report.totalIncome)
        assertEquals(BigDecimal.valueOf(500), report.totalExpenses)
        assertEquals(BigDecimal.valueOf(1500), report.netSavings)
        assertEquals(1, report.incomeByCategory.size)
        assertEquals("Salary", report.incomeByCategory[0].categoryName)
        assertEquals(BigDecimal.valueOf(2000), report.incomeByCategory[0].totalAmount)
    }
}
