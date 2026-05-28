package com.example.backend.user.service

import com.example.backend.shared.exceptions.DuplicateResourceException
import com.example.backend.shared.exceptions.NotFoundException
import com.example.backend.shared.security.SecurityHelper
import com.example.backend.user.dto.UpdateUserRequest
import com.example.backend.user.dto.UserResponse
import com.example.backend.user.mapper.UserMapper
import com.example.backend.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val securityHelper: SecurityHelper,
    private val passwordEncoder: PasswordEncoder,
) {
    fun getCurrentUser(): UserResponse {
        val email = securityHelper.currentUserEmail()
        val user = userRepository.findByEmail(email).orElseThrow { NotFoundException("User not found") }
        return UserMapper.toResponse(user)
    }

    fun getUserById(id: Long): UserResponse {
        val user = userRepository.findById(id).orElseThrow { NotFoundException("User not found") }
        return UserMapper.toResponse(user)
    }

    @Transactional
    fun updateUser(request: UpdateUserRequest): UserResponse {
        val email = securityHelper.currentUserEmail()
        val user = userRepository.findByEmail(email).orElseThrow { NotFoundException("User not found") }

        request.email?.lowercase()?.let { newEmail ->
            if (newEmail != user.email && userRepository.existsByEmail(newEmail)) {
                throw DuplicateResourceException("Email already exists")
            }
            user.email = newEmail
        }

        request.username?.let { newUsername ->
            if (newUsername != user.username && userRepository.existsByUsername(newUsername)) {
                throw DuplicateResourceException("Username already exists")
            }
            user.username = newUsername
        }

        request.password?.let { user.password = passwordEncoder.encode(it) }

        return UserMapper.toResponse(user)
    }

    @Transactional
    fun deleteUser() {
        val email = securityHelper.currentUserEmail()
        val user = userRepository.findByEmail(email).orElseThrow { NotFoundException("User not found") }
        userRepository.delete(user)
    }
}
