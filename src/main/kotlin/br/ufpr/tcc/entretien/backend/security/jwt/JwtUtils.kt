package br.ufpr.tcc.entretien.backend.security.jwt

import java.util.Date;

import br.ufpr.tcc.entretien.backend.service.UserDetailsImpl
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.security.core.Authentication;
import io.jsonwebtoken.*

@Component
class JwtUtils {
    @Value("\${entretien.app.jwtSecret}")
    lateinit var jwtSecret: String

    @Value("\${entretien.app.jwtExpirationMs}")
    var jwtExpirationMs: Long = 0

    fun generateJwtToken(authentication: Authentication): String {
        println("[LOG] generateJwtToken()")

        val userPrincipal: UserDetailsImpl = authentication.principal as UserDetailsImpl

        return Jwts.builder()
            .setSubject(userPrincipal.username)
            .setIssuedAt(Date())
            .setExpiration(Date(Date().time + jwtExpirationMs))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact()
    }

    fun getUserNameFromJwtToken(token: String): String {
        println("[LOG] getUserNameFromJwtToken()")
        val username = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).body.subject
        println("[LOG] username: $username")
        return username
    }

    fun validateJwtToken(authToken: String?): Boolean {
        println("[LOG] validateJwtToken()")
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken)
            return true
        } catch (e: SignatureException) {
            println("[LOG] Invalid JWT signature: {}" + e.message)
//            logger.error("Invalid JWT signature: {}", e.getMessage())
        } catch (e: MalformedJwtException) {
            println("[LOG] Invalid JWT token: {}" + e.message)
//            logger.error("Invalid JWT token: {}", e.getMessage())
        } catch (e: ExpiredJwtException) {
            println("[LOG] JWT token is expired: {}" + e.message)
//            logger.error("JWT token is expired: {}", e.getMessage())
        } catch (e: UnsupportedJwtException) {
            println("[LOG] JWT token is unsupported: {}" + e.message)
//            logger.error("JWT token is unsupported: {}", e.getMessage())
        } catch (e: IllegalArgumentException) {
            println("[LOG] JWT claims string is empty: {}" + e.message)
//            logger.error("JWT claims string is empty: {}", e.message)
        }
        return false
    }
}