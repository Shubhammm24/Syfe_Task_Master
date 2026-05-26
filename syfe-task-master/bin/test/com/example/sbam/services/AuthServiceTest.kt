package com.example.sbam.services

import com.example.sbam.dtos.RegisterRequest
import com.example.sbam.exceptions.ConflictException
import com.example.sbam.models.User
import com.example.sbam.repositories.UserRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class AuthServiceTest {

    private val userRepository = mock(UserRepository::class.java)
    private val authService = AuthService(userRepository)

    @Test
    fun testRegisterSuccess() {
        val request = RegisterRequest("test@email.com", "password", "Test User", "1234567890")
        `when`(userRepository.findByUsername("test@email.com")).thenReturn(null)
        `when`(userRepository.save(any(User::class.java))).thenAnswer { it.arguments[0] as User }

        val user = authService.register(request)
        assertEquals("test@email.com", user.username)
        assertEquals("Test User", user.fullName)
        assertEquals("1234567890", user.phoneNumber)
        assertTrue(authService.getPasswordEncoder().matches("password", user.password))
    }

    @Test
    fun testRegisterDuplicateUsernameThrowsConflict() {
        val request = RegisterRequest("test@email.com", "password", "Test User", "1234567890")
        val existingUser = User(1L, "test@email.com", "hashed", "Test User", "1234567890")
        `when`(userRepository.findByUsername("test@email.com")).thenReturn(existingUser)

        assertThrows(ConflictException::class.java) {
            authService.register(request)
        }
    }

    @Test
    fun testFindByUsername() {
        val existingUser = User(1L, "test@email.com", "hashed", "Test User", "1234567890")
        `when`(userRepository.findByUsername("test@email.com")).thenReturn(existingUser)

        val user = authService.findByUsername("test@email.com")
        assertNotNull(user)
        assertEquals("test@email.com", user?.username)
    }
}
