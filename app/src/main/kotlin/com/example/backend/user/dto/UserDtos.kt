package com.example.backend.user.dto

import com.example.backend.user.entity.UserRole
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.Instant

data class UserResponse(
    val id: Long,
    val email: String,
    val username: String,
    val role: UserRole,
    val createdAt: Instant,
)

data class UpdateUserRequest(
    @field:Email(message = "email must be valid")
    val email: String?,

    @field:Pattern(
        regexp = "^[a-zA-Z0-9_]{3,30}$",
        message = "username must be 3-30 chars and contain only letters, digits, underscore",
    )
    val username: String?,

    @field:Size(min = 8, message = "password must have at least 8 characters")
    val password: String?,
)
