package br.ufpr.tcc.entretien.backend.controller

import br.ufpr.tcc.entretien.backend.common.logger.LOGGER
import br.ufpr.tcc.entretien.backend.datasource.request.CandidateResumeRequest
import br.ufpr.tcc.entretien.backend.datasource.request.CandidateSignupRequest
import br.ufpr.tcc.entretien.backend.datasource.request.UpdateCandidateDataRequest
import br.ufpr.tcc.entretien.backend.model.users.Candidate
import br.ufpr.tcc.entretien.backend.service.CandidateService
import br.ufpr.tcc.entretien.backend.service.ResumeService
import br.ufpr.tcc.entretien.backend.service.UserDetailsImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.time.LocalDate
import java.util.*
import javax.validation.Valid

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/candidate")
class CandidateController {
    companion object {
        private const val LOG_TAG = "entretien-backend-candidate-controller"
        private val logger = LOGGER.getLogger(AuthController::class.java)
    }

    @Autowired
    lateinit var candidateService: CandidateService

    @Autowired
    lateinit var resumeService: ResumeService

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
            candidateService.createNewCandidate(candidate)
            ResponseEntity.ok<Any>("User registered successfully!")
        } catch (ex: Exception) {
            println("[ERROR] ------------------------------------------")
            println(ex.message)
            ResponseEntity.internalServerError().body("Persistence error.")
        }
    }

    @GetMapping("/resume")
    @PreAuthorize("hasRole('ROLE_CANDIDATE')")
    fun getMyResume(authentication: Authentication): ResponseEntity<Any> {
        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val candidateId = userDetails.getId()
        return ResponseEntity.ok(resumeService.getResumeByCandidateId(candidateId))
    }

    @PostMapping("/resume")
    @PreAuthorize("hasRole('ROLE_CANDIDATE')")
    fun saveMyNewResume(
        @Valid @RequestBody candidateResumeRequest: CandidateResumeRequest,
        authentication: Authentication
    ): ResponseEntity<Any> {
        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val candidateId = userDetails.getId()
        val candidate = candidateService.getCandidateById(candidateId)
        val resume = resumeService.buildResume(candidateResumeRequest, candidate)
        return ResponseEntity.ok(resume)
    }

    @PutMapping("/resume")
    @PreAuthorize("hasRole('ROLE_CANDIDATE')")
    fun updateMyResume(
        @Valid @RequestBody candidateResumeRequest: CandidateResumeRequest,
        authentication: Authentication
    ): ResponseEntity<Any> {
        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val candidateId = userDetails.getId()
        val candidate = candidateService.getCandidateById(candidateId)
        val resume = resumeService.updateResume(candidateResumeRequest, candidate)
        return ResponseEntity.ok(resume)
    }

    @GetMapping("/resume/last-update")
    @PreAuthorize("hasRole('ROLE_CANDIDATE')")
    fun getCandidateResumeLastUpdate(
        authentication: Authentication
    ): ResponseEntity<Any> {
        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val candidateId = userDetails.getId()

        return ResponseEntity.ok(resumeService.getCandidateResumeLastUpdate(candidateId))
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

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_CANDIDATE')")
    fun getMe(
        authentication: Authentication
    ): ResponseEntity<Candidate> {
            logger.info(LOG_TAG, "getMe")
        try {
            val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
            val candidateId = userDetails.getId()
            logger.info(LOG_TAG, "received request from user", mapOf("user-id" to candidateId.toString()))
            val me = candidateService.getCandidateById(candidateId)
            return ResponseEntity.ok(me)
        } catch (ex: Exception) {
            logger.error(LOG_TAG, ex.message, ex.stackTrace)
            throw IllegalArgumentException()
        }
    }

    @PutMapping("")
    @PreAuthorize("hasRole('ROLE_CANDIDATE')")
    fun updateCandidate(
        @Valid @RequestBody updateCandidateDataRequest: UpdateCandidateDataRequest,
        authentication: Authentication
    ): ResponseEntity<Any> {
        logger.info(LOG_TAG, "updateCandidateDataRequest.pcd " + updateCandidateDataRequest.pcd)
        try {
            val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
            val candidateId = userDetails.getId()
            var candidate = candidateService.getCandidateById(candidateId)
            candidate.firstName = updateCandidateDataRequest.firstName
            candidate.lastName = updateCandidateDataRequest.lastName
            candidate.phone = updateCandidateDataRequest.phone
            candidate.email = updateCandidateDataRequest.email
            candidate.cep = updateCandidateDataRequest.cep
            candidate.socialNetworking = updateCandidateDataRequest.socialNetworking
            candidate.birthDay = updateCandidateDataRequest.birthDay
            candidate.pcd = updateCandidateDataRequest.pcd
            candidate.cep = updateCandidateDataRequest.cep
            candidate.updatedAt = Date.from(Instant.now())
            logger.info(LOG_TAG, "received request from user", mapOf("user-id" to candidateId.toString()))
            val me = candidateService.update(candidate);
            return ResponseEntity.ok(me)
        } catch (ex: Exception) {
            logger.error(LOG_TAG, ex.message, ex.stackTrace)
            throw IllegalArgumentException()
        }
    }

    @PreAuthorize("hasRole('ROLE_CANDIDATE')")
    @GetMapping("/dashboard")
    fun getRecruiterDashboard(
        @RequestParam(value = "from") @DateTimeFormat(pattern = "yyyy-MM-dd")
        from: LocalDate,
        @RequestParam(value = "to") @DateTimeFormat(pattern = "yyyy-MM-dd")
        to: LocalDate,
        authentication: Authentication
    ): ResponseEntity<*> {
        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val candidateId = userDetails.getId()

        return ResponseEntity.ok(candidateService.getDashboard(candidateId, from, to))
    }

}