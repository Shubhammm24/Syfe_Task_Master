package com.example.sbam.controllers

import com.example.sbam.dtos.LoginRequest
import com.example.sbam.dtos.RegisterRequest
import com.example.sbam.dtos.UserResponse
import com.example.sbam.exceptions.UnauthorizedException
import com.example.sbam.services.AuthService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
    private val securityContextRepository: SecurityContextRepository
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<UserResponse> {
        val user = authService.register(request)
        val response = UserResponse(
            id = user.id!!,
            username = user.username,
            fullName = user.fullName,
            phoneNumber = user.phoneNumber
        )
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest,
        servletRequest: HttpServletRequest,
        servletResponse: HttpServletResponse
    ): ResponseEntity<UserResponse> {
        val user = authService.findByUsername(request.username)
            ?: throw UnauthorizedException("Invalid username or password")

        if (!authService.getPasswordEncoder().matches(request.password, user.password)) {
            throw UnauthorizedException("Invalid username or password")
        }

        val auth = UsernamePasswordAuthenticationToken(
            user.username,
            null,
            listOf(SimpleGrantedAuthority("ROLE_USER"))
        )

        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = auth
        SecurityContextHolder.setContext(context)
        securityContextRepository.saveContext(context, servletRequest, servletResponse)

        val response = UserResponse(
            id = user.id!!,
            username = user.username,
            fullName = user.fullName,
            phoneNumber = user.phoneNumber
        )
        return ResponseEntity.ok(response)
    }

    @PostMapping("/logout")
    fun logout(request: HttpServletRequest): ResponseEntity<Map<String, String>> {
        val session = request.getSession(false)
        session?.invalidate()
        SecurityContextHolder.clearContext()
        return ResponseEntity.ok(mapOf("message" to "Successfully logged out"))
    }
}
