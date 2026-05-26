package com.example.sbam.services

import com.example.sbam.dtos.GoalRequest
import com.example.sbam.exceptions.BadRequestException
import com.example.sbam.models.Category
import com.example.sbam.models.CategoryType
import com.example.sbam.models.Goal
import com.example.sbam.models.Transaction
import com.example.sbam.models.User
import com.example.sbam.repositories.GoalRepository
import com.example.sbam.repositories.TransactionRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import java.math.BigDecimal
import java.time.LocalDate

class GoalServiceTest {

    private val goalRepository = mock(GoalRepository::class.java)
    private val transactionRepository = mock(TransactionRepository::class.java)
    private val categoryService = mock(CategoryService::class.java)
    private val goalService = GoalService(goalRepository, transactionRepository, categoryService)

    private val user = User(1L, "user@email.com", "hashed", "User Name", "1234")

    @BeforeEach
    fun setup() {
        `when`(categoryService.getAuthenticatedUser()).thenReturn(user)
    }

    @Test
    fun testCreateGoalSuccess() {
        val request = GoalRequest("Save for Car", BigDecimal.valueOf(5000), LocalDate.now().plusMonths(6))
        `when`(goalRepository.save(any(Goal::class.java))).thenAnswer { it.arguments[0] as Goal }

        val goal = goalService.createGoal(request)
        assertEquals("Save for Car", goal.goalName)
        assertEquals(BigDecimal.valueOf(5000), goal.targetAmount)
        assertEquals(LocalDate.now().plusMonths(6), goal.targetDate)
        assertEquals(user, goal.user)
    }

    @Test
    fun testCreateGoalPastDateThrowsBadRequest() {
        val request = GoalRequest("Save for Car", BigDecimal.valueOf(5000), LocalDate.now().minusDays(1))

        assertThrows(BadRequestException::class.java) {
            goalService.createGoal(request)
        }
    }
}
