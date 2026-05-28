package com.example.backend.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class RegisterRequest(
    @field:Email(message = "email must be valid")
    val email: String,

    @field:Pattern(
        regexp = "^[a-zA-Z0-9_]{3,30}$",
        message = "username must be 3-30 chars and contain only letters, digits, underscore",
    )
    val username: String,

    @field:Size(min = 8, message = "password must have at least 8 characters")
    val password: String,
)

data class LoginRequest(
    @field:NotBlank(message = "email is required")
    val email: String,

    @field:NotBlank(message = "password is required")
    val password: String,
)

data class AuthResponse(
    val token: String,
    val tokenType: String = "Bearer",
    val expiresInSeconds: Long,
)
