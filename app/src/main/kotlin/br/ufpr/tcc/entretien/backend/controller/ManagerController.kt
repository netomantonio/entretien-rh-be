package br.ufpr.tcc.entretien.backend.controller

import br.ufpr.tcc.entretien.backend.datasource.request.ManagerSignupRequest
import br.ufpr.tcc.entretien.backend.service.ManagerService
import br.ufpr.tcc.entretien.backend.service.UserDetailsImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
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
}