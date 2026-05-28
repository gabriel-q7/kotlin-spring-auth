package com.example.backend.auth.controller

import com.example.backend.auth.dto.AuthResponse
import com.example.backend.auth.dto.LoginRequest
import com.example.backend.auth.dto.RegisterRequest
import com.example.backend.auth.service.AuthService
import com.example.backend.shared.utils.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<ApiResponse<AuthResponse>> =
        ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse(true, "User registered", authService.register(request)))

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<ApiResponse<AuthResponse>> =
        ResponseEntity.ok(ApiResponse(true, "Login successful", authService.login(request)))
}
