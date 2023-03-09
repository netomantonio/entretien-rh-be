package br.ufpr.tcc.entretien.backend.controller

import br.ufpr.tcc.entretien.backend.datasource.request.CandidateResumeRequest
import br.ufpr.tcc.entretien.backend.datasource.request.CandidateSignupRequest
import br.ufpr.tcc.entretien.backend.model.users.Candidate
import br.ufpr.tcc.entretien.backend.service.CandidateService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
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

        val candidate = candidateService.build(candidateSignupRequest)

        return try {
            candidateService.register(candidate)
            ResponseEntity.ok<Any>("User registered successfully!")
        } catch (ex: Exception) {
            println("[ERROR] ------------------------------------------")
            println(ex.message)
            ResponseEntity.internalServerError().body("Persistence error.")
        }
    }

    @PostMapping("/resume")
    @PreAuthorize(
        "hasRole('ROLE_ADMIN') or hasRole('ROLE_CANDIDATE') and #candidateResumeRequest.candidateId == principal.id")
    fun saveResume(@Valid @RequestBody candidateResumeRequest: CandidateResumeRequest): ResponseEntity<*> {
        var candidate = candidateService.getCandidateById(candidateResumeRequest.candidateId)

        candidate.resume = candidateService.buildResume(
            candidateResumeRequest.presentation,
            candidateResumeRequest.educationLevel,
            candidateResumeRequest.professionalHistory,
            candidateResumeRequest.languages,
            candidateResumeRequest.desiredJobTitle,
            candidate
        )

        return try {
            candidateService.register(candidate)
            ResponseEntity.ok<Any>("User updated successfully!")
        } catch (ex: Exception) {
            println("[ERROR] ------------------------------------------")
            println(ex.message)
            ResponseEntity.internalServerError().body("Persistence error.")
        }
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECRUITER')")
    fun getAllCandidates(): ResponseEntity<*> {
        return try {
            val candidates = candidateService.getAllCandidates()
            ResponseEntity.ok<Iterable<Candidate>>(candidates)
        } catch (ex: Exception) {
            println("[ERROR] ------------------------------------------")
            println(ex.message)
            ResponseEntity.internalServerError().body("Something went wrong.")
        }
    }

}