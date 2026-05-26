package com.example.sbam.services

import com.example.sbam.dtos.TransactionRequest
import com.example.sbam.exceptions.BadRequestException
import com.example.sbam.models.Category
import com.example.sbam.models.CategoryType
import com.example.sbam.models.Transaction
import com.example.sbam.models.User
import com.example.sbam.repositories.CategoryRepository
import com.example.sbam.repositories.TransactionRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.math.BigDecimal
import java.time.LocalDate

class TransactionServiceTest {

    private val transactionRepository = mock(TransactionRepository::class.java)
    private val categoryRepository = mock(CategoryRepository::class.java)
    private val categoryService = mock(CategoryService::class.java)
    private val transactionService = TransactionService(transactionRepository, categoryRepository, categoryService)

    private val user = User(1L, "user@email.com", "hashed", "User Name", "1234")
    private val category = Category(1L, "Food", CategoryType.EXPENSE, false, null)

    @BeforeEach
    fun setup() {
        `when`(categoryService.getAuthenticatedUser()).thenReturn(user)
    }

    @Test
    fun testCreateTransactionSuccess() {
        val request = TransactionRequest(BigDecimal.TEN, LocalDate.now(), "Food", "Dinner")
        `when`(categoryRepository.findByNameIgnoreCaseAndUser("Food", user)).thenReturn(category)
        `when`(transactionRepository.save(any(Transaction::class.java))).thenAnswer { it.arguments[0] as Transaction }

        val tx = transactionService.createTransaction(request)
        assertEquals(BigDecimal.TEN, tx.amount)
        assertEquals(category, tx.category)
        assertEquals(user, tx.user)
        assertEquals("Dinner", tx.description)
    }

    @Test
    fun testCreateTransactionFutureDateThrowsBadRequest() {
        val request = TransactionRequest(BigDecimal.TEN, LocalDate.now().plusDays(1), "Food", "Dinner")

        assertThrows(BadRequestException::class.java) {
            transactionService.createTransaction(request)
        }
    }
}
