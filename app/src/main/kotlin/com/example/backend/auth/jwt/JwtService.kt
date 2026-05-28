package com.example.backend.auth.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("\${app.jwt.secret}") private val jwtSecret: String,
    @Value("\${app.jwt.expiration-ms:3600000}") private val jwtExpirationMs: Long,
) {
    fun generateToken(subject: String, additionalClaims: Map<String, Any> = emptyMap()): String {
        val now = Instant.now()
        val expiry = now.plusMillis(jwtExpirationMs)

        return Jwts.builder()
            .claims(additionalClaims)
            .subject(subject)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .signWith(signingKey())
            .compact()
    }

    fun isTokenValid(token: String, subject: String): Boolean {
        val claims = extractAllClaims(token)
        return claims.subject == subject && !isTokenExpired(claims)
    }

    fun extractSubject(token: String): String = extractAllClaims(token).subject

    fun extractExpiration(token: String): Date = extractAllClaims(token).expiration

    fun getExpirationSeconds(): Long = jwtExpirationMs / 1000

    private fun extractAllClaims(token: String): Claims =
        Jwts.parser().verifyWith(signingKey()).build().parseSignedClaims(token).payload

    private fun isTokenExpired(claims: Claims): Boolean = claims.expiration.before(Date())

    private fun signingKey(): SecretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret))
}
