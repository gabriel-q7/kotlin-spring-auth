package com.example.backend.user.mapper

import com.example.backend.user.dto.UserResponse
import com.example.backend.user.entity.UserEntity

object UserMapper {
    fun toResponse(entity: UserEntity): UserResponse = UserResponse(
        id = requireNotNull(entity.id),
        email = entity.email,
        username = entity.username,
        role = entity.role,
        createdAt = requireNotNull(entity.createdAt),
    )
}
