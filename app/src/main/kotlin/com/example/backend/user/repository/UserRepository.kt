package com.example.backend.user.repository

import com.example.backend.user.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByEmail(email: String): Optional<UserEntity>
    fun findByUsername(username: String): Optional<UserEntity>
    fun existsByEmail(email: String): Boolean
    fun existsByUsername(username: String): Boolean
}
