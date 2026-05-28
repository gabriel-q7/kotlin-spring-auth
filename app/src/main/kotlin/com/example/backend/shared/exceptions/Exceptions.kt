package com.example.backend.shared.exceptions

open class ApiException(message: String) : RuntimeException(message)

class NotFoundException(message: String) : ApiException(message)

class DuplicateResourceException(message: String) : ApiException(message)

class AuthenticationException(message: String) : ApiException(message)
