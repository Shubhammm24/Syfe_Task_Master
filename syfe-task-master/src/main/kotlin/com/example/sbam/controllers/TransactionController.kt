package com.example.sbam.controllers

import com.example.sbam.dtos.CategoryResponse
import com.example.sbam.dtos.TransactionRequest
import com.example.sbam.dtos.TransactionResponse
import com.example.sbam.models.CategoryType
import com.example.sbam.models.Transaction
import com.example.sbam.services.TransactionService
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@RequestMapping("/api/transactions")
class TransactionController(
    private val transactionService: TransactionService
) {

    private fun mapToResponse(tx: Transaction): TransactionResponse {
        return TransactionResponse(
            id = tx.id!!,
            amount = tx.amount,
            date = tx.date,
            description = tx.description,
            category = CategoryResponse(
                id = tx.category.id!!,
                name = tx.category.name,
                type = tx.category.type,
                isCustom = tx.category.isCustom
            )
        )
    }

    @PostMapping
    fun createTransaction(@Valid @RequestBody request: TransactionRequest): ResponseEntity<TransactionResponse> {
        val tx = transactionService.createTransaction(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(mapToResponse(tx))
    }

    @GetMapping
    fun getTransactions(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate?,
        @RequestParam(required = false) category: String?,
        @RequestParam(required = false) type: CategoryType?
    ): ResponseEntity<List<TransactionResponse>> {
        val transactions = transactionService.getTransactions(startDate, endDate, category, type)
        return ResponseEntity.ok(transactions.map { mapToResponse(it) })
    }

    @PutMapping("/{id}")
    fun updateTransaction(
        @PathVariable id: Long,
        @Valid @RequestBody request: TransactionRequest
    ): ResponseEntity<TransactionResponse> {
        val tx = transactionService.updateTransaction(id, request)
        return ResponseEntity.ok(mapToResponse(tx))
    }

    @DeleteMapping("/{id}")
    fun deleteTransaction(@PathVariable id: Long): ResponseEntity<Map<String, String>> {
        transactionService.deleteTransaction(id)
        return ResponseEntity.ok(mapOf("message" to "Transaction deleted successfully"))
    }
}
