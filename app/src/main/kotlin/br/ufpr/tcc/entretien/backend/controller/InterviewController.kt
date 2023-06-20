package br.ufpr.tcc.entretien.backend.controller

import br.ufpr.tcc.entretien.backend.common.exception.interview.UserIsNotAuthorizedException
import br.ufpr.tcc.entretien.backend.datasource.request.CommitInterviewRequest
import br.ufpr.tcc.entretien.backend.datasource.request.InterviewRequest
import br.ufpr.tcc.entretien.backend.model.interview.Interview
import br.ufpr.tcc.entretien.backend.service.InterviewService
import br.ufpr.tcc.entretien.backend.service.UserDetailsImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.util.Optional
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

        val candidateCpf: String = interviewRequest.candidateCpf

        val managerObservation: String = interviewRequest.managerObservation

        val managerId = userDetails.getId()
        return try {
            interviewService.createInterview(candidateCpf, managerObservation, managerId)
            ResponseEntity.ok<Any>("Interview registered successfully!")
        } catch (ex: Exception) {
            println("[ERROR] ------------------------------------------")
            println(ex.message)
            ex.printStackTrace()
            ResponseEntity.internalServerError().body("Persistence error.")
        }
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PutMapping("/{id}")
    fun updateInterview(
        @Valid @RequestBody interview: Interview,
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<*> {

        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val managerId = userDetails.getId()
        val optInterview: Optional<Interview> = interviewService.getInterview(id)

        return try {
            val dbInterview = optInterview.get()
            if (dbInterview.manager.id != managerId) return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)
            if (dbInterview.getId() != interview.getId()) return ResponseEntity<Any>(HttpStatus.FORBIDDEN)
            ResponseEntity<Any>(interviewService.updateInterview(interview), HttpStatus.OK)
        } catch (e: NoSuchElementException) {
            ResponseEntity<Any>(HttpStatus.NOT_FOUND)
        } catch (e: Exception) {
            ResponseEntity<Any>(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @PutMapping("/adjust/{id}")
    fun adjustInterview(
        @Valid @RequestBody interviewRequest: InterviewRequest,
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<*> {

        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val managerId = userDetails.getId()
        val optInterview: Optional<Interview> = interviewService.getInterview(id)

        return try {
            val dbInterview = optInterview.get()
            if (dbInterview.manager.id != managerId) return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)
            if (dbInterview.getId() != id) return ResponseEntity<Any>(HttpStatus.FORBIDDEN)
            if (interviewRequest.candidateCpf.isEmpty() && interviewRequest.managerObservation.isEmpty()) return ResponseEntity<Any>(
                "Dados para atualização não podem estar vazios!",
                HttpStatus.BAD_REQUEST
            )
            if (interviewRequest.candidateCpf.isNotEmpty() && dbInterview.candidate != null) return ResponseEntity<Any>(
                "Não é possível alterar o CPF quando já existe um candidato associado!",
                HttpStatus.FORBIDDEN
            )
            ResponseEntity<Any>(
                interviewService.adjustInterview(
                    dbInterview,
                    interviewRequest.candidateCpf,
                    interviewRequest.managerObservation
                ), HttpStatus.OK
            )
        } catch (e: NoSuchElementException) {
            ResponseEntity<Any>(HttpStatus.NOT_FOUND)
        } catch (e: Exception) {
            ResponseEntity<Any>(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    fun getInterview(
        @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<*> {
        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val userId = userDetails.getId()
        val optInterview: Optional<Interview> = interviewService.getInterview(id)
        return try {
            val interview = optInterview.get()
            if (interviewService.isInterviewRelated(userId, interview))
                ResponseEntity<Any>(interview, HttpStatus.OK)
            else
                ResponseEntity<Any>(HttpStatus.FORBIDDEN)
        } catch (ex: Exception) {
            ResponseEntity<Any>(ex.message, HttpStatus.INTERNAL_SERVER_ERROR)
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
        val date = commitInterviewRequest.date
        val candidateId = userDetails.getId()
        val interviewId = commitInterviewRequest.interviewId

        if (interviewService.getInterview(interviewId).get().candidate!!.id != candidateId) throw UserIsNotAuthorizedException()
        val scheduleId = commitInterviewRequest.scheduleId
        return try {
            interviewService.commitInterview(scheduleId, interviewId, date)
            ResponseEntity.ok<Any>("Interview updated")
        } catch (ex: Exception) {
            println("[ERROR] ------------------------------------------")
            println(ex.message)
            ex.printStackTrace()
            ResponseEntity.internalServerError().body("Persistence error.")
        }
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @DeleteMapping("/{id}")
    fun deleteInterview(
        @Valid @PathVariable id: Long,
        authentication: Authentication
    ): ResponseEntity<*> {
        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val managerId = userDetails.getId()
        val optInterview: Optional<Interview> = interviewService.getInterview(id)
        return try {
            val dbInterview = optInterview.get()
            if (dbInterview.manager.id != managerId) return ResponseEntity<Any>(HttpStatus.UNAUTHORIZED)
            if (!interviewService.canDelete(dbInterview)) return ResponseEntity<Any>(
                "The interview can no longer be deleted!",
                HttpStatus.FORBIDDEN
            )
            ResponseEntity<Any>(
                interviewService.deleteInterview(dbInterview), HttpStatus.OK
            )
        } catch (e: NoSuchElementException) {
            ResponseEntity<Any>(HttpStatus.NOT_FOUND)
        } catch (e: Exception) {
            ResponseEntity<Any>(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PreAuthorize("hasRole('ROLE_CANDIDATE')")
    @GetMapping("/candidate/period")
    fun getCandidateInterviewsWithinPeriod(
        @RequestParam(value = "from") @DateTimeFormat(pattern = "yyyy-MM-dd")
        from: LocalDate,
        @RequestParam(value = "to") @DateTimeFormat(pattern = "yyyy-MM-dd")
        to: LocalDate,
        authentication: Authentication
    ): ResponseEntity<*>{
        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val candidateId = userDetails.getId()
        return ResponseEntity.ok<Any>(interviewService.getCandidateInterviewsWithinPeriod(candidateId, from, to))
    }

    @PreAuthorize("hasRole('ROLE_CANDIDATE')")
    @GetMapping("/candidate/next")
    fun getNextCandidateInterview(
        authentication: Authentication
    ): ResponseEntity<*> {
        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val candidateId = userDetails.getId()
        return ResponseEntity.ok<Any>(interviewService.getCandidateNextInterview(candidateId))
    }

    @PreAuthorize("hasRole('ROLE_CANDIDATE')")
    @GetMapping("/candidate/stats")
    fun getCandidateInterviewStats(
        authentication: Authentication
    ): ResponseEntity<*> {
        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val candidateId = userDetails.getId()
        return ResponseEntity.ok<Any>(interviewService.getCandidateInterviewNumbers(candidateId))
    }

    @PreAuthorize("hasRole('ROLE_RECRUITER')")
    @GetMapping("/recruiter/next")
    fun getNextRecruiterInterview(
        authentication: Authentication
    ): ResponseEntity<*> {
        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val recruiterId = userDetails.getId()
        return ResponseEntity.ok<Any>(interviewService.getRecruiterNextInterview(recruiterId))
    }

    @PreAuthorize("hasRole('ROLE_RECRUITER')")
    @GetMapping("/recruiter/period")
    fun getRecruiterInterviewsWithinPeriod(
        @RequestParam(value = "from") @DateTimeFormat(pattern = "yyyy-MM-dd")
        from: LocalDate,
        @RequestParam(value = "to") @DateTimeFormat(pattern = "yyyy-MM-dd")
        to: LocalDate,
        authentication: Authentication
    ): ResponseEntity<*>{
        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val recruiterId = userDetails.getId()
        return ResponseEntity.ok<Any>(interviewService.getRecruiterInterviewsWithinPeriod(recruiterId, from, to))
    }
}