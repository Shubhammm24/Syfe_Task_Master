package com.example.sbam.services

import com.example.sbam.dtos.RegisterRequest
import com.example.sbam.exceptions.ConflictException
import com.example.sbam.models.User
import com.example.sbam.repositories.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository
) {
    private val passwordEncoder = BCryptPasswordEncoder()

    fun register(request: RegisterRequest): User {
        if (userRepository.findByUsername(request.username) != null) {
            throw ConflictException("Username (email) is already registered")
        }
        val user = User(
            username = request.username,
            password = passwordEncoder.encode(request.password),
            fullName = request.fullName,
            phoneNumber = request.phoneNumber
        )
        return userRepository.save(user)
    }

    fun findByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    fun getPasswordEncoder(): BCryptPasswordEncoder {
        return passwordEncoder
    }
}
