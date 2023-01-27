package br.ufpr.tcc.entretien.backend.controller

import br.ufpr.tcc.entretien.backend.datasource.request.CandidateSignupRequest
import br.ufpr.tcc.entretien.backend.service.CandidateService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/candidate")
class CandidateController {

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
            println(ex.message)
            ResponseEntity.internalServerError().body("Persistence error.")
        }

    }

}