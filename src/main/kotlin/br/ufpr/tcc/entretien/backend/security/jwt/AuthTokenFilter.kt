package br.ufpr.tcc.entretien.backend.security.jwt

import br.ufpr.tcc.entretien.backend.service.UserDetailsServiceImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import javax.servlet.FilterChain
import javax.servlet.ServletException
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse


class AuthTokenFilter : OncePerRequestFilter() {
    @Autowired
    lateinit var jwtUtils: JwtUtils

    @Autowired
    lateinit var userDetailsService: UserDetailsServiceImpl

//    private val logger: Logger = LoggerFactory.getLogger(AuthTokenFilter::class.java)

    private fun parseJwt(request: HttpServletRequest): String? {
        println("[LOG] parseJwt()")
        val headerAuth = request.getHeader("Authorization")
        return if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            println("[LOG] headerAuth.substring(7, headerAuth.length)" + headerAuth.substring(7, headerAuth.length))
            headerAuth.substring(7, headerAuth.length)
        } else null
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        println("[LOG] doFilterInternal()")
        try {
            val jwt = parseJwt(request)
            if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
                println("[LOG] jwt != null && jwtUtils.validateJwtToken(jwt)")
                val username: String = jwtUtils.getUserNameFromJwtToken(jwt)

                val userDetails = userDetailsService.loadUserByUsername(username)
                val authentication = UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails?.authorities
                )
                println("[LOG] username: " + username)
                println("[LOG] userDetails.authorities: " + userDetails?.authorities.toString())
                println("[LOG] authentication.isAuthenticated: " + authentication.isAuthenticated)

                authentication.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: Exception) {
            println("[LOG] Cannot set user authentication: {}" + e.message)
//            logger.error("Cannot set user authentication: {}", e)
        }
        filterChain.doFilter(request, response)
    }
}