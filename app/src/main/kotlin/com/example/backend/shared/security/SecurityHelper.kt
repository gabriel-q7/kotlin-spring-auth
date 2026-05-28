package com.example.backend.shared.security

import com.example.backend.shared.exceptions.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityHelper {
    fun currentUserEmail(): String {
        val auth = SecurityContextHolder.getContext().authentication
        val principal = auth?.name
        if (principal.isNullOrBlank() || principal == "anonymousUser") {
            throw AuthenticationException("Unauthorized")
        }
        return principal
    }
}
