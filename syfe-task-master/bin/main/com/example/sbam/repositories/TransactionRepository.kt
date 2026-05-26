package com.example.sbam.repositories

import com.example.sbam.models.Category
import com.example.sbam.models.CategoryType
import com.example.sbam.models.Transaction
import com.example.sbam.models.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface TransactionRepository : JpaRepository<Transaction, Long> {
    fun existsByCategory(category: Category): Boolean

    @Query("SELECT t FROM Transaction t WHERE t.user = :user " +
           "AND (:startDate IS NULL OR t.date >= :startDate) " +
           "AND (:endDate IS NULL OR t.date <= :endDate) " +
           "AND (:categoryName IS NULL OR LOWER(t.category.name) = LOWER(:categoryName)) " +
           "AND (:type IS NULL OR t.category.type = :type) " +
           "ORDER BY t.date DESC, t.id DESC")
    fun findFilteredTransactions(
        user: User,
        startDate: LocalDate?,
        endDate: LocalDate?,
        categoryName: String?,
        type: CategoryType?
    ): List<Transaction>

    fun findByUserAndDateGreaterThanEqual(user: User, date: LocalDate): List<Transaction>
}
