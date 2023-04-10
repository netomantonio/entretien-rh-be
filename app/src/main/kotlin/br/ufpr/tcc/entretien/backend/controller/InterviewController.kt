package br.ufpr.tcc.entretien.backend.controller

import br.ufpr.tcc.entretien.backend.datasource.request.CommitInterviewRequest
import br.ufpr.tcc.entretien.backend.datasource.request.InterviewRequest
import br.ufpr.tcc.entretien.backend.service.InterviewService
import br.ufpr.tcc.entretien.backend.service.UserDetailsImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/interview")
class InterviewController {

    @Autowired
    lateinit var interviewService: InterviewService

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PostMapping("")
    fun createNewInterview(
        @Valid @RequestBody interviewRequest: InterviewRequest,
        authentication: Authentication
    ): ResponseEntity<*> {
        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl

        val managerId = userDetails.getId()
        return try {
            interviewService.createInterview(interviewRequest, managerId)
            ResponseEntity.ok<Any>("Interview registered successfully!")
        } catch (ex: Exception) {
            println("[ERROR] ------------------------------------------")
            println(ex.message)
            ex.printStackTrace()
            ResponseEntity.internalServerError().body("Persistence error.")
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("")
    fun getAllInterviews(): ResponseEntity<*> {
        return try {
            val interviews = interviewService.getAll()
            ResponseEntity.ok<Any>(interviews)
        } catch (ex: Exception) {
            println("[ERROR] ------------------------------------------")
            println(ex.message)
            ex.printStackTrace()
            ResponseEntity.internalServerError().body("Persistence error.")
        }
    }


    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/manager")
    fun getAllInterviewsByManager(authentication: Authentication): ResponseEntity<*> {

        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl

        return try {
            val interviews = interviewService.getAllByManager(userDetails.getId())
            ResponseEntity.ok<Any>(interviews)
        } catch (ex: Exception) {
            println("[ERROR] ------------------------------------------")
            println(ex.message)
            ex.printStackTrace()
            ResponseEntity.internalServerError().body("Persistence error.")
        }
    }

    @PreAuthorize("hasRole('ROLE_CANDIDATE')")
    @PostMapping("/commit")
    fun commitInterview(
        @Valid @RequestBody commitInterviewRequest: CommitInterviewRequest,
        authentication: Authentication
    ): ResponseEntity<*> {
        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val candidateId = userDetails.getId()
        val date = commitInterviewRequest.date
        val scheduleId = commitInterviewRequest.scheduleId
        return try {
            interviewService.commitInterview(scheduleId, date, candidateId)
            ResponseEntity.ok<Any>("Interview updated")
        } catch (ex: Exception) {
            println("[ERROR] ------------------------------------------")
            println(ex.message)
            ex.printStackTrace()
            ResponseEntity.internalServerError().body("Persistence error.")
        }
    }
}