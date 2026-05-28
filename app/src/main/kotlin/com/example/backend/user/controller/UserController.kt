package com.example.backend.user.controller

import com.example.backend.shared.utils.ApiResponse
import com.example.backend.user.dto.UpdateUserRequest
import com.example.backend.user.dto.UserResponse
import com.example.backend.user.service.UserService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService,
) {
    @GetMapping("/me")
    fun getCurrentUser(): ResponseEntity<ApiResponse<UserResponse>> =
        ResponseEntity.ok(ApiResponse(true, "Current user", userService.getCurrentUser()))

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<ApiResponse<UserResponse>> =
        ResponseEntity.ok(ApiResponse(true, "User found", userService.getUserById(id)))

    @PutMapping("/me")
    fun updateCurrentUser(@Valid @RequestBody request: UpdateUserRequest): ResponseEntity<ApiResponse<UserResponse>> =
        ResponseEntity.ok(ApiResponse(true, "User updated", userService.updateUser(request)))

    @DeleteMapping("/me")
    fun deleteCurrentUser(): ResponseEntity<ApiResponse<Nothing>> {
        userService.deleteUser()
        return ResponseEntity.ok(ApiResponse(success = true, message = "User deleted"))
    }
}
