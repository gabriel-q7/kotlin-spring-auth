package com.example.backend.shared.utils

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
)
