package com.example.sbam.dtos

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDate

data class TransactionRequest(
    @field:NotNull(message = "Amount is required")
    @field:Positive(message = "Amount must be a positive decimal value")
    val amount: BigDecimal,

    @field:NotNull(message = "Date is required")
    val date: LocalDate,

    @field:NotBlank(message = "Category name is required")
    val categoryName: String,

    val description: String? = null
)

data class TransactionResponse(
    val id: Long,
    val amount: BigDecimal,
    val date: LocalDate,
    val description: String?,
    val category: CategoryResponse
)
