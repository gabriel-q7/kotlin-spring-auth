package com.example.backend.shared.exceptions

import com.example.backend.shared.utils.ApiResponse
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationError(ex: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Map<String, String?>>> {
        val details = ex.bindingResult.allErrors.associate { error ->
            val key = (error as? FieldError)?.field ?: error.objectName
            key to error.defaultMessage
        }
        return ResponseEntity.badRequest().body(ApiResponse(false, "Validation failed", details))
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(ex: ConstraintViolationException): ResponseEntity<ApiResponse<String>> =
        ResponseEntity.badRequest().body(ApiResponse(false, ex.message ?: "Invalid request"))

    @ExceptionHandler(DuplicateResourceException::class)
    fun handleDuplicate(ex: DuplicateResourceException): ResponseEntity<ApiResponse<String>> =
        ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse(false, ex.message ?: "Duplicate resource"))

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException): ResponseEntity<ApiResponse<String>> =
        ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse(false, ex.message ?: "Not found"))

    @ExceptionHandler(AuthenticationException::class, BadCredentialsException::class)
    fun handleAuthentication(ex: Exception): ResponseEntity<ApiResponse<String>> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse(false, ex.message ?: "Unauthorized"))

    @ExceptionHandler(AccessDeniedException::class)
    fun handleForbidden(ex: AccessDeniedException): ResponseEntity<ApiResponse<String>> =
        ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse(false, ex.message ?: "Forbidden"))

    @ExceptionHandler(Exception::class)
    fun handleUnknown(ex: Exception): ResponseEntity<ApiResponse<String>> =
        ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ApiResponse(false, ex.message ?: "Unexpected error"))
}
