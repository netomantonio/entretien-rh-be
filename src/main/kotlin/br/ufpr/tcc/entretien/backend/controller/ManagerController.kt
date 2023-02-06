package br.ufpr.tcc.entretien.backend.controller

import br.ufpr.tcc.entretien.backend.datasource.request.ManagerSignupRequest
import br.ufpr.tcc.entretien.backend.service.ManagerService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

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
}