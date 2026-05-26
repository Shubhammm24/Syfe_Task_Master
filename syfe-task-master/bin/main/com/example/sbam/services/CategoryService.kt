package com.example.sbam.services

import com.example.sbam.dtos.CustomCategoryRequest
import com.example.sbam.exceptions.BadRequestException
import com.example.sbam.exceptions.ConflictException
import com.example.sbam.exceptions.ForbiddenException
import com.example.sbam.exceptions.ResourceNotFoundException
import com.example.sbam.exceptions.UnauthorizedException
import com.example.sbam.models.Category
import com.example.sbam.models.CategoryType
import com.example.sbam.models.User
import com.example.sbam.repositories.CategoryRepository
import com.example.sbam.repositories.TransactionRepository
import com.example.sbam.repositories.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CategoryService(
    private val categoryRepository: CategoryRepository,
    private val userRepository: UserRepository,
    private val transactionRepository: TransactionRepository
) {

    fun getAuthenticatedUser(): User {
        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication == null || !authentication.isAuthenticated || authentication.name == "anonymousUser") {
            throw UnauthorizedException("User is not authenticated")
        }
        return userRepository.findByUsername(authentication.name)
            ?: throw UnauthorizedException("User not found")
    }

    @Transactional
    fun seedDefaultCategories() {
        val defaults = listOf(
            Pair("Salary", CategoryType.INCOME),
            Pair("Food", CategoryType.EXPENSE),
            Pair("Rent", CategoryType.EXPENSE),
            Pair("Transportation", CategoryType.EXPENSE),
            Pair("Entertainment", CategoryType.EXPENSE),
            Pair("Healthcare", CategoryType.EXPENSE),
            Pair("Utilities", CategoryType.EXPENSE)
        )
        for ((name, type) in defaults) {
            if (categoryRepository.findByNameIgnoreCaseAndUserNull(name) == null) {
                categoryRepository.save(Category(name = name, type = type, isCustom = false, user = null))
            }
        }
    }

    fun getCategoriesForCurrentUser(): List<Category> {
        val user = getAuthenticatedUser()
        return categoryRepository.findByUserOrUserNull(user)
    }

    @Transactional
    fun createCustomCategory(request: CustomCategoryRequest): Category {
        val user = getAuthenticatedUser()

        // Check if a global category exists with this name (case-insensitive)
        if (categoryRepository.findByNameIgnoreCaseAndUserNull(request.name) != null) {
            throw ConflictException("A default category with name '${request.name}' already exists")
        }

        // Check if user already has a custom category with this name (case-insensitive)
        if (categoryRepository.findByNameIgnoreCaseAndUser(request.name, user) != null) {
            throw ConflictException("A custom category with name '${request.name}' already exists for this user")
        }

        val category = Category(
            name = request.name,
            type = request.type,
            isCustom = true,
            user = user
        )
        return categoryRepository.save(category)
    }

    @Transactional
    fun deleteCustomCategory(name: String) {
        val user = getAuthenticatedUser()

        // Find the custom category belonging to the user
        val category = categoryRepository.findByNameIgnoreCaseAndUser(name, user)
            ?: run {
                // If there's a global category with this name, throw conflict/bad request because defaults cannot be deleted
                if (categoryRepository.findByNameIgnoreCaseAndUserNull(name) != null) {
                    throw BadRequestException("Default categories cannot be deleted or modified")
                }
                throw ResourceNotFoundException("Custom category '$name' not found for this user")
            }

        // Ensure category is custom and belongs to the user
        if (!category.isCustom) {
            throw BadRequestException("Default categories cannot be deleted")
        }
        if (category.user?.id != user.id) {
            throw ForbiddenException("You cannot delete this category")
        }

        // Check if category is referenced by any transactions
        if (transactionRepository.existsByCategory(category)) {
            throw ConflictException("Category is currently referenced by transactions and cannot be deleted")
        }

        categoryRepository.delete(category)
    }
}
