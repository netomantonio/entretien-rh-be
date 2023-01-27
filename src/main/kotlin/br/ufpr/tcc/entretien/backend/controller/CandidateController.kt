package br.ufpr.tcc.entretien.backend.controller

import br.ufpr.tcc.entretien.backend.datasource.request.CandidateSignupRequest
import br.ufpr.tcc.entretien.backend.model.Candidate
import br.ufpr.tcc.entretien.backend.model.enums.ERole
import br.ufpr.tcc.entretien.backend.model.Role
import br.ufpr.tcc.entretien.backend.repository.RoleRepository
import br.ufpr.tcc.entretien.backend.repository.UserRepository
import br.ufpr.tcc.entretien.backend.service.CandidateService
import br.ufpr.tcc.entretien.backend.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/candidate")
class CandidateController {

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var candidateService: CandidateService

    @PostMapping("")
    fun registerCandidate(@Valid @RequestBody candidateSignupRequest: CandidateSignupRequest): ResponseEntity<*> {
        if (candidateSignupRequest != null) {
            if (candidateService.existsByUsername(candidateSignupRequest.username)) {
                return ResponseEntity
                    .badRequest()
                    .body<Any>(("Error: Username is already taken!"))
            }
            if (candidateService.existsByEmail(candidateSignupRequest.email)) {
                return ResponseEntity
                    .badRequest()
                    .body<Any>("Error: Email is already in use!")
            }
        }

        val candidate = candidateService.buildCandidate(candidateSignupRequest)

        return try {
            candidateService.registerCandidate(candidate)
            ResponseEntity.ok<Any>("User registered successfully!")
        } catch (ex: Exception) {
            println("[ERROR] ------------------------------------------")
            println("[MESSAGE]")
            println(ex.message)
            ResponseEntity.internalServerError().body("Persistence error.")
        }

    }

}