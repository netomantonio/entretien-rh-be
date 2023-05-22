package br.ufpr.tcc.entretien.backend.security.jwt

import br.ufpr.tcc.entretien.backend.common.exception.jwt.InvalidTokenException
import br.ufpr.tcc.entretien.backend.common.exception.jwt.TokenGeneratorException
import br.ufpr.tcc.entretien.backend.common.logger.LOGGER
import br.ufpr.tcc.entretien.backend.service.UserDetailsImpl
import io.jsonwebtoken.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtils {
    companion object {
        private const val LOG_TAG = "entretien-backend-jwt-utils"
        private val logger = LOGGER.getLogger(JwtUtils::class.java)
    }

    @Value("\${entretien.app.jwtSecret}")
    lateinit var jwtSecret: String

    @Value("\${entretien.app.jwtExpirationMs}")
    var jwtExpirationMs: Long = 0

    fun generateJwtToken(authentication: Authentication): String {
        try {
            val userPrincipal: UserDetailsImpl = authentication.principal as UserDetailsImpl
            val token = Jwts.builder()
                .setSubject(userPrincipal.username)
                .setIssuedAt(Date())
                .setExpiration(Date(Date().time + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact()
            logger.info(LOG_TAG, "successfully generated jwt token")
            return token
        } catch (ex: Exception) {
            logger.error(LOG_TAG, "error when trying to generate jwt token", ex.stackTrace)
            throw TokenGeneratorException("error when trying to generate jwt token", ex)
        }
    }

    fun getUserNameFromJwtToken(token: String): String {
        val username = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).body.subject
        logger.info(LOG_TAG, "successfully generated jwt token")
        return username

    }

    fun validateJwtToken(authToken: String?): Boolean {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken)
            logger.info(LOG_TAG, "Token Is Valid")
            return true
        } catch (ex: Exception) {
            when(ex) {
                is ExpiredJwtException,
                is UnsupportedJwtException,
                is MalformedJwtException,
                is SignatureException,
                is IllegalArgumentException -> {
                    logger.error(LOG_TAG, "unprocessed token", ex.stackTrace, mapOf("token" to authToken.toString()))
                    throw InvalidTokenException()
                }
                else -> {
                    throw Exception(ex)
                }
            }
        }
    }
}