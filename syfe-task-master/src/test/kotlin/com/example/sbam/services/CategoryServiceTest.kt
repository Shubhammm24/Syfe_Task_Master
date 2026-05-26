package com.example.sbam.services

import com.example.sbam.dtos.CustomCategoryRequest
import com.example.sbam.exceptions.BadRequestException
import com.example.sbam.exceptions.ConflictException
import com.example.sbam.models.Category
import com.example.sbam.models.CategoryType
import com.example.sbam.models.User
import com.example.sbam.repositories.CategoryRepository
import com.example.sbam.repositories.TransactionRepository
import com.example.sbam.repositories.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder

class CategoryServiceTest {

    private val categoryRepository = mock(CategoryRepository::class.java)
    private val userRepository = mock(UserRepository::class.java)
    private val transactionRepository = mock(TransactionRepository::class.java)
    private val categoryService = CategoryService(categoryRepository, userRepository, transactionRepository)

    private val user = User(1L, "user@email.com", "hashed", "User Name", "1234")

    @BeforeEach
    fun setup() {
        val authentication = mock(Authentication::class.java)
        val securityContext = mock(SecurityContext::class.java)
        `when`(authentication.name).thenReturn("user@email.com")
        `when`(authentication.isAuthenticated).thenReturn(true)
        `when`(securityContext.authentication).thenReturn(authentication)
        SecurityContextHolder.setContext(securityContext)

        `when`(userRepository.findByUsername("user@email.com")).thenReturn(user)
    }

    @Test
    fun testGetCategoriesForCurrentUser() {
        val list = listOf(Category(1L, "Food", CategoryType.EXPENSE, false, null))
        `when`(categoryRepository.findByUserOrUserNull(user)).thenReturn(list)

        val result = categoryService.getCategoriesForCurrentUser()
        assertEquals(1, result.size)
        assertEquals("Food", result[0].name)
    }

    @Test
    fun testCreateCustomCategorySuccess() {
        val request = CustomCategoryRequest("Bonus", CategoryType.INCOME)
        `when`(categoryRepository.findByNameIgnoreCaseAndUserNull("Bonus")).thenReturn(null)
        `when`(categoryRepository.findByNameIgnoreCaseAndUser("Bonus", user)).thenReturn(null)
        `when`(categoryRepository.save(any(Category::class.java))).thenAnswer { it.arguments[0] as Category }

        val category = categoryService.createCustomCategory(request)
        assertEquals("Bonus", category.name)
        assertEquals(CategoryType.INCOME, category.type)
        assertTrue(category.isCustom)
        assertEquals(user, category.user)
    }

    @Test
    fun testCreateCustomCategoryDuplicateDefaultThrowsConflict() {
        val request = CustomCategoryRequest("Food", CategoryType.EXPENSE)
        `when`(categoryRepository.findByNameIgnoreCaseAndUserNull("Food")).thenReturn(Category(1L, "Food", CategoryType.EXPENSE, false, null))

        assertThrows(ConflictException::class.java) {
            categoryService.createCustomCategory(request)
        }
    }

    @Test
    fun testDeleteDefaultCategoryThrowsBadRequest() {
        `when`(categoryRepository.findByNameIgnoreCaseAndUser("Food", user)).thenReturn(null)
        `when`(categoryRepository.findByNameIgnoreCaseAndUserNull("Food")).thenReturn(Category(1L, "Food", CategoryType.EXPENSE, false, null))

        assertThrows(BadRequestException::class.java) {
            categoryService.deleteCustomCategory("Food")
        }
    }

    @Test
    fun testDeleteCustomCategoryReferencedByTransactionThrowsConflict() {
        val customCat = Category(2L, "Gym", CategoryType.EXPENSE, true, user)
        `when`(categoryRepository.findByNameIgnoreCaseAndUser("Gym", user)).thenReturn(customCat)
        `when`(transactionRepository.existsByCategory(customCat)).thenReturn(true)

        assertThrows(ConflictException::class.java) {
            categoryService.deleteCustomCategory("Gym")
        }
    }
}
