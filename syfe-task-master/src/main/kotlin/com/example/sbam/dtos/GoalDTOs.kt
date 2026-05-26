package com.example.sbam.dtos

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDate

data class GoalRequest(
    @field:NotBlank(message = "Goal name is required")
    val goalName: String,

    @field:NotNull(message = "Target amount is required")
    @field:Positive(message = "Target amount must be a positive decimal value")
    val targetAmount: BigDecimal,

    @field:NotNull(message = "Target date is required")
    val targetDate: LocalDate
)

data class GoalResponse(
    val id: Long,
    val goalName: String,
    val targetAmount: BigDecimal,
    val targetDate: LocalDate,
    val startDate: LocalDate,
    val currentProgress: BigDecimal,
    val progressPercentage: Double,
    val remainingAmount: BigDecimal
)
