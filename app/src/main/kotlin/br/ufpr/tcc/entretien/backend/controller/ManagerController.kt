package br.ufpr.tcc.entretien.backend.controller

import br.ufpr.tcc.entretien.backend.common.logger.LOGGER
import br.ufpr.tcc.entretien.backend.datasource.request.ManagerSignupRequest
import br.ufpr.tcc.entretien.backend.datasource.request.ManagerUpdateModelRequest
import br.ufpr.tcc.entretien.backend.model.users.Manager
import br.ufpr.tcc.entretien.backend.service.ManagerService
import br.ufpr.tcc.entretien.backend.service.UserDetailsImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import javax.validation.Valid

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/manager")
class ManagerController {
    companion object {
        private const val LOG_TAG = "entretien-backend-manager-controller"
        private val logger = LOGGER.getLogger(ManagerController::class.java)
    }

    @Autowired
    lateinit var managerService: ManagerService

    @PostMapping("")
    fun registerManager(@Valid @RequestBody managerSignupRequest: ManagerSignupRequest): ResponseEntity<*> {

        if (managerSignupRequest != null) {
            if (managerService.existsByUsername(managerSignupRequest.username)) {
                return ResponseEntity
                    .badRequest()
                    .body<Any>(("Error: Username is already taken!"))
            }
            if (managerService.existsByEmail(managerSignupRequest.email)) {
                return ResponseEntity
                    .badRequest()
                    .body<Any>("Error: Email is already in use!")
            }
        }

        val recruiter = managerService.build(managerSignupRequest)

        return try {
            managerService.register(recruiter)
            ResponseEntity.ok<Any>("Manager registered successfully!")
        } catch (ex: Exception) {
            println("[ERROR] ------------------------------------------")
            println(ex.message)
            ResponseEntity.internalServerError().body("Persistence error.")
        }
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/dashboard")
    fun getAllRecruiterDashboard(
        @RequestParam(value = "from") @DateTimeFormat(pattern = "yyyy-MM-dd")
        from: LocalDate,
        @RequestParam(value = "to") @DateTimeFormat(pattern = "yyyy-MM-dd")
        to: LocalDate,
        authentication: Authentication
    ): ResponseEntity<*> {
        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val managerId = userDetails.getId()

        return ResponseEntity.ok(managerService.getDashboard(managerId, from, to))
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_MANAGER')")
    fun getMe(
        authentication: Authentication
    ): ResponseEntity<Manager> {
        logger.info(LOG_TAG, "getMe")
        try {
            val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
            val managerId = userDetails.getId()
            logger.info(LOG_TAG, "received request from user", mapOf("user-id" to managerId.toString()))
            val me = managerService.getManagerById(managerId)
            return ResponseEntity.ok(me)
        } catch (ex: Exception) {
            logger.error(LOG_TAG, ex.message, ex.stackTrace)
            throw IllegalArgumentException()
        }
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PutMapping
    fun updated(
        @Valid @RequestBody managerUpdateRequest: ManagerUpdateModelRequest,
        authentication: Authentication
    ): ResponseEntity<String?>? {
        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val managerId = userDetails.getId()
        logger.info(LOG_TAG, "receive request update manager user", mapOf("user-id" to managerId.toString()))
        try {
            val managerUpdated = managerUpdateRequest.toModel(managerService.getManagerById(managerId))
            managerService.update(managerUpdated)
            return ResponseEntity(HttpStatus.OK)
        } catch (ex: Exception) {
            throw Exception()
        }
    }
}