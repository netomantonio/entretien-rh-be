package br.ufpr.tcc.entretien.backend.controller

import br.ufpr.tcc.entretien.backend.common.logger.LOGGER
import br.ufpr.tcc.entretien.backend.datasource.request.LoginRequest
import br.ufpr.tcc.entretien.backend.datasource.response.JwtResponse
import br.ufpr.tcc.entretien.backend.model.users.User
import br.ufpr.tcc.entretien.backend.repository.RoleRepository
import br.ufpr.tcc.entretien.backend.repository.UserRepository
import br.ufpr.tcc.entretien.backend.security.jwt.JwtUtils
import br.ufpr.tcc.entretien.backend.service.UserDetailsImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.lang.Exception
import java.util.stream.Collectors
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid


@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
class AuthController {
    companion object {
        private const val LOG_TAG = "entretien-backend-auth-controller"
        private val logger = LOGGER.getLogger(AuthController::class.java)
    }

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    @Autowired
    lateinit var userRepository: UserRepository<User>

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var encoder: PasswordEncoder

    @Autowired
    lateinit var jwtUtils: JwtUtils

    @PostMapping("/signin")
    fun authenticateUser(@Valid @RequestBody loginRequest: LoginRequest, request: HttpServletRequest):Any {

        try {
            val authentication: Authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(loginRequest.username, loginRequest.password)
            )

            logger.info(LOG_TAG,"Authentication Successfull", mapOf("username" to loginRequest.username))

            SecurityContextHolder.getContext().authentication = authentication
            val jwt = jwtUtils.generateJwtToken(authentication)

            val userDetails = authentication.principal as UserDetailsImpl
            val roles: List<String> = userDetails.authorities.stream()
                .map { item: GrantedAuthority -> item.authority }
                .collect(Collectors.toList())

            val response = JwtResponse(
                jwt,
                userDetails.getId(),
                userDetails.username,
                userDetails.getEmail(),
                roles
            )

            return ResponseEntity.ok<Any>(
                response
            )
        } catch (ex: Exception) {
            logger.error(LOG_TAG, ex.message, ex.stackTrace)
            throw Exception(ex)
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/roles")
    fun listAllRoles(): ResponseEntity<*> {
        // TODO: move to service
        return ResponseEntity.ok<Any>(roleRepository.findAll().toString())
    }
}
