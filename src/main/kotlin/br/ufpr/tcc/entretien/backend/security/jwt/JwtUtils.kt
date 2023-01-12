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
        val userPrincipal: UserDetailsImpl = authentication.getPrincipal() as UserDetailsImpl
        return Jwts.builder()
            .setSubject(userPrincipal.username)
            .setIssuedAt(Date())
            .setExpiration(Date(Date().time + jwtExpirationMs))
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact()
    }

    fun getUserNameFromJwtToken(token: String): String {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject()
    }

    fun validateJwtToken(authToken: String?): Boolean {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken)
            return true
        } catch (e: SignatureException) {
//            logger.error("Invalid JWT signature: {}", e.getMessage())
        } catch (e: MalformedJwtException) {
//            logger.error("Invalid JWT token: {}", e.getMessage())
        } catch (e: ExpiredJwtException) {
//            logger.error("JWT token is expired: {}", e.getMessage())
        } catch (e: UnsupportedJwtException) {
//            logger.error("JWT token is unsupported: {}", e.getMessage())
        } catch (e: IllegalArgumentException) {
//            logger.error("JWT claims string is empty: {}", e.message)
        }
        return false
    }
}