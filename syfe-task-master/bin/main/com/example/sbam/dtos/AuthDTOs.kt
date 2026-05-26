package com.example.sbam.dtos

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:NotBlank(message = "Username (email) is required")
    @field:Email(message = "Username must be a valid email address")
    val username: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 6, message = "Password must be at least 6 characters long")
    val password: String,

    @field:NotBlank(message = "Full name is required")
    val fullName: String,

    @field:NotBlank(message = "Phone number is required")
    val phoneNumber: String
)

data class LoginRequest(
    @field:NotBlank(message = "Username is required")
    val username: String,

    @field:NotBlank(message = "Password is required")
    val password: String
)

data class UserResponse(
    val id: Long,
    val username: String,
    val fullName: String,
    val phoneNumber: String
)
