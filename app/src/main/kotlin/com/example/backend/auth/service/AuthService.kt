package com.example.backend.auth.service

import com.example.backend.auth.dto.AuthResponse
import com.example.backend.auth.dto.LoginRequest
import com.example.backend.auth.dto.RegisterRequest
import com.example.backend.auth.jwt.JwtService
import com.example.backend.shared.exceptions.AuthenticationException
import com.example.backend.shared.exceptions.DuplicateResourceException
import com.example.backend.user.entity.UserEntity
import com.example.backend.user.entity.UserRole
import com.example.backend.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService,
) {
    fun register(request: RegisterRequest): AuthResponse {
        if (userRepository.existsByEmail(request.email)) {
            throw DuplicateResourceException("Email already exists")
        }
        if (userRepository.existsByUsername(request.username)) {
            throw DuplicateResourceException("Username already exists")
        }

        val user = UserEntity(
            email = request.email.lowercase(),
            username = request.username,
            password = passwordEncoder.encode(request.password),
            role = UserRole.USER,
        )
        val savedUser = userRepository.save(user)
        return buildAuthResponse(savedUser.email, savedUser.role.name)
    }

    fun login(request: LoginRequest): AuthResponse {
        val user = userRepository.findByEmail(request.email.lowercase())
            .orElseThrow { AuthenticationException("Invalid credentials") }

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw AuthenticationException("Invalid credentials")
        }

        return buildAuthResponse(user.email, user.role.name)
    }

    fun generateToken(email: String, role: String): AuthResponse = buildAuthResponse(email, role)

    private fun buildAuthResponse(email: String, role: String): AuthResponse {
        val token = jwtService.generateToken(email, mapOf("role" to role))
        return AuthResponse(token = token, expiresInSeconds = jwtService.getExpirationSeconds())
    }
}
