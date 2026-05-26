package com.example.sbam.services

import com.example.sbam.dtos.GoalRequest
import com.example.sbam.dtos.GoalResponse
import com.example.sbam.exceptions.BadRequestException
import com.example.sbam.exceptions.ForbiddenException
import com.example.sbam.exceptions.ResourceNotFoundException
import com.example.sbam.models.CategoryType
import com.example.sbam.models.Goal
import com.example.sbam.repositories.GoalRepository
import com.example.sbam.repositories.TransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@Service
class GoalService(
    private val goalRepository: GoalRepository,
    private val transactionRepository: TransactionRepository,
    private val categoryService: CategoryService
) {

    @Transactional
    fun createGoal(request: GoalRequest): Goal {
        val user = categoryService.getAuthenticatedUser()

        if (!request.targetDate.isAfter(LocalDate.now())) {
            throw BadRequestException("Target date must be a future date")
        }

        val goal = Goal(
            goalName = request.goalName,
            targetAmount = request.targetAmount,
            targetDate = request.targetDate,
            startDate = LocalDate.now(),
            user = user
        )
        return goalRepository.save(goal)
    }

    fun getGoals(): List<GoalResponse> {
        val user = categoryService.getAuthenticatedUser()
        val goals = goalRepository.findByUser(user)
        return goals.map { calculateProgress(it) }
    }

    fun getGoalById(id: Long): GoalResponse {
        val user = categoryService.getAuthenticatedUser()
        val goal = goalRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Goal with ID $id not found")
        }
        if (goal.user.id != user.id) {
            throw ForbiddenException("You do not have permission to view this goal")
        }
        return calculateProgress(goal)
    }

    @Transactional
    fun updateGoal(id: Long, request: GoalRequest): Goal {
        val user = categoryService.getAuthenticatedUser()

        val goal = goalRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Goal with ID $id not found")
        }
        if (goal.user.id != user.id) {
            throw ForbiddenException("You do not have permission to update this goal")
        }

        if (!request.targetDate.isAfter(LocalDate.now())) {
            throw BadRequestException("Target date must be a future date")
        }

        goal.goalName = request.goalName
        goal.targetAmount = request.targetAmount
        goal.targetDate = request.targetDate

        return goalRepository.save(goal)
    }

    @Transactional
    fun deleteGoal(id: Long) {
        val user = categoryService.getAuthenticatedUser()

        val goal = goalRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Goal with ID $id not found")
        }
        if (goal.user.id != user.id) {
            throw ForbiddenException("You do not have permission to delete this goal")
        }

        goalRepository.delete(goal)
    }

    fun calculateProgress(goal: Goal): GoalResponse {
        val transactions = transactionRepository.findByUserAndDateGreaterThanEqual(goal.user, goal.startDate)
        var totalIncome = BigDecimal.ZERO
        var totalExpense = BigDecimal.ZERO

        for (tx in transactions) {
            if (tx.category.type == CategoryType.INCOME) {
                totalIncome = totalIncome.add(tx.amount)
            } else {
                totalExpense = totalExpense.add(tx.amount)
            }
        }

        val currentProgress = totalIncome.subtract(totalExpense)
        val remainingAmount = goal.targetAmount.subtract(currentProgress).coerceAtLeast(BigDecimal.ZERO)
        val progressPercentage = if (goal.targetAmount.compareTo(BigDecimal.ZERO) > 0) {
            (currentProgress.toDouble() / goal.targetAmount.toDouble() * 100.0).coerceAtLeast(0.0)
        } else {
            0.0
        }

        return GoalResponse(
            id = goal.id!!,
            goalName = goal.goalName,
            targetAmount = goal.targetAmount,
            targetDate = goal.targetDate,
            startDate = goal.startDate,
            currentProgress = currentProgress,
            progressPercentage = progressPercentage,
            remainingAmount = remainingAmount
        )
    }
}
