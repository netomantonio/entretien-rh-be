package br.ufpr.tcc.entretien.backend.security.jwt

import br.ufpr.tcc.entretien.backend.common.logger.LOGGER
import br.ufpr.tcc.entretien.backend.service.UserDetailsServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class AuthTokenFilter : OncePerRequestFilter() {

    companion object {
        private val log = LOGGER.getLogger(AuthTokenFilter::class.java)
        private const val LOG_TAG = "entretien-backend-auth-token-filter"
    }

    @Autowired
    lateinit var jwtUtils: JwtUtils

    @Autowired
    lateinit var userDetailsService: UserDetailsServiceImpl

    private fun parseJwt(request: HttpServletRequest): String? {
        val headerAuth = request.getHeader("Authorization")
        return if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ") && !headerAuth.contains("null")) {
            log.info(LOG_TAG,"Parse Successful", mapOf("headerAuth" to headerAuth.substring(7, 14) + "**********"))
            headerAuth.substring(7, headerAuth.length)
        } else if (headerAuth.contains("null")) {
            log.info(LOG_TAG,"Parse Successful", mapOf("headerAuth" to headerAuth.substring(7, headerAuth.length)))
            headerAuth.substring(7, headerAuth.length)
        } else {
            null
        }
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val jwt = parseJwt(request)
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                val username: String = jwtUtils.getUserNameFromJwtToken(jwt)

                val userDetails = userDetailsService.loadUserByUsername(username)
                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails?.authorities
                )
                log.info(
                    LOG_TAG,
                    "Get Token Successful",
                    mapOf(
                        "username" to username,
                        "authorities" to userDetails?.authorities.toString(),
                        "isAuthenticated" to authentication.isAuthenticated.toString()
                    )
                )
                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: Exception) {
            log.error(LOG_TAG, "Cannot set user authentication", e.stackTrace)
            throw Exception(e)
        }
        filterChain.doFilter(request, response)
    }
}