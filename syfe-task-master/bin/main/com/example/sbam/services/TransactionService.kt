package com.example.sbam.services

import com.example.sbam.dtos.TransactionRequest
import com.example.sbam.exceptions.BadRequestException
import com.example.sbam.exceptions.ForbiddenException
import com.example.sbam.exceptions.ResourceNotFoundException
import com.example.sbam.models.CategoryType
import com.example.sbam.models.Transaction
import com.example.sbam.repositories.CategoryRepository
import com.example.sbam.repositories.TransactionRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class TransactionService(
    private val transactionRepository: TransactionRepository,
    private val categoryRepository: CategoryRepository,
    private val categoryService: CategoryService
) {

    @Transactional
    fun createTransaction(request: TransactionRequest): Transaction {
        val user = categoryService.getAuthenticatedUser()

        if (request.date.isAfter(LocalDate.now())) {
            throw BadRequestException("Transaction date cannot be in the future")
        }

        // Find category (either system or user's custom category)
        val category = categoryRepository.findByNameIgnoreCaseAndUser(request.categoryName, user)
            ?: categoryRepository.findByNameIgnoreCaseAndUserNull(request.categoryName)
            ?: throw BadRequestException("Category '${request.categoryName}' is not accessible or does not exist")

        val transaction = Transaction(
            amount = request.amount,
            date = request.date,
            description = request.description,
            category = category,
            user = user
        )
        return transactionRepository.save(transaction)
    }

    fun getTransactions(
        startDate: LocalDate?,
        endDate: LocalDate?,
        categoryName: String?,
        type: CategoryType?
    ): List<Transaction> {
        val user = categoryService.getAuthenticatedUser()
        return transactionRepository.findFilteredTransactions(user, startDate, endDate, categoryName, type)
    }

    @Transactional
    fun updateTransaction(id: Long, request: TransactionRequest): Transaction {
        val user = categoryService.getAuthenticatedUser()

        val transaction = transactionRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Transaction with ID $id not found")
        }

        if (transaction.user.id != user.id) {
            throw ForbiddenException("You do not have permission to update this transaction")
        }

        // Find category
        val category = categoryRepository.findByNameIgnoreCaseAndUser(request.categoryName, user)
            ?: categoryRepository.findByNameIgnoreCaseAndUserNull(request.categoryName)
            ?: throw BadRequestException("Category '${request.categoryName}' is not accessible or does not exist")

        // Modify all fields EXCEPT the date field
        transaction.amount = request.amount
        transaction.description = request.description
        transaction.category = category

        return transactionRepository.save(transaction)
    }

    @Transactional
    fun deleteTransaction(id: Long) {
        val user = categoryService.getAuthenticatedUser()

        val transaction = transactionRepository.findById(id).orElseThrow {
            ResourceNotFoundException("Transaction with ID $id not found")
        }

        if (transaction.user.id != user.id) {
            throw ForbiddenException("You do not have permission to delete this transaction")
        }

        transactionRepository.delete(transaction)
    }
}
