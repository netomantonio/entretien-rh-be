package br.ufpr.tcc.entretien.backend.controller

import br.ufpr.tcc.entretien.backend.common.exception.interview.UserIsNotAuthorizedException
import br.ufpr.tcc.entretien.backend.common.logger.LOGGER
import br.ufpr.tcc.entretien.backend.datasource.request.CommitInterviewRequest
import br.ufpr.tcc.entretien.backend.datasource.request.UpdateParcialInterviewRequest
import br.ufpr.tcc.entretien.backend.datasource.request.InterviewRequest
import br.ufpr.tcc.entretien.backend.datasource.response.InterviewsByCandidateResponse
import br.ufpr.tcc.entretien.backend.model.enums.InterviewStatusTypes
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
import java.util.*
import java.time.LocalDate
import javax.validation.Valid

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/interview")
class InterviewController {

    companion object {
        private const val LOG_TAG = "entretien-backend-interview-controller"
        private val logger = LOGGER.getLogger(AuthController::class.java)
    }

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

        val recruiterObservation: String = interviewRequest.recruiterObservation

        val managerId = userDetails.getId()
        return try {
            interviewService.createInterview(candidateCpf, recruiterObservation, managerId)
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
            if (interviewRequest.candidateCpf.isEmpty() && interviewRequest.recruiterObservation.isEmpty()) return ResponseEntity<Any>(
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
                    interviewRequest.recruiterObservation
                ), HttpStatus.OK
            )
        } catch (e: NoSuchElementException) {
            ResponseEntity<Any>(HttpStatus.NOT_FOUND)
        } catch (e: Exception) {
            ResponseEntity<Any>(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER') or hasRole('ROLE_RECRUITER') or hasRole('ROLE_CANDIDATE')")
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
            if (interviewService.isInterviewRelated(userId, interview) || userDetails.isAdmin())
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

        if (interviewService.getInterview(interviewId)
                .get().candidate!!.id != candidateId
        ) throw UserIsNotAuthorizedException()
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
    ): ResponseEntity<*> {
        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val candidateId = userDetails.getId()
        return ResponseEntity.ok<Any>(interviewService.getCandidateInterviewsWithinPeriod(candidateId, from, to))
    }

    @PreAuthorize("hasRole('ROLE_RECRUITER')")
    @GetMapping("/recruiter/period")
    fun getRecruiterInterviewsWithinPeriod(
        @RequestParam(value = "from") @DateTimeFormat(pattern = "yyyy-MM-dd")
        from: LocalDate,
        @RequestParam(value = "to") @DateTimeFormat(pattern = "yyyy-MM-dd")
        to: LocalDate,
        authentication: Authentication
    ): ResponseEntity<*> {
        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val recruiterId = userDetails.getId()
        return ResponseEntity.ok<Any>(interviewService.getRecruiterInterviewsWithinPeriod(recruiterId, from, to))
    }

    @PreAuthorize("hasRole('ROLE_MANAGER')")
    @GetMapping("/manager/period")
    fun getManagerInterviewsWithinPeriod(
        @RequestParam(value = "from") @DateTimeFormat(pattern = "yyyy-MM-dd")
        from: LocalDate,
        @RequestParam(value = "to") @DateTimeFormat(pattern = "yyyy-MM-dd")
        to: LocalDate,
        authentication: Authentication
    ): ResponseEntity<*> {
        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val managerId = userDetails.getId()
        return ResponseEntity.ok<Any>(interviewService.getManagerInterviewsWithinPeriod(managerId, from, to))
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/period")
    fun getInterviewsWithinPeriod(
        @RequestParam(value = "from") @DateTimeFormat(pattern = "yyyy-MM-dd")
        from: LocalDate,
        @RequestParam(value = "to") @DateTimeFormat(pattern = "yyyy-MM-dd")
        to: LocalDate,
    ): ResponseEntity<*> {
        return ResponseEntity.ok<Any>(interviewService.getInterviewsWithinPeriod(from, to))
    }

    @PreAuthorize("hasRole('ROLE_RECRUITER') or hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    fun updateParcialInterview(
        @Valid @PathVariable id: Long,
        @Valid @RequestBody updateParcialInterviewRequest: UpdateParcialInterviewRequest,
        authentication: Authentication
    ): ResponseEntity<*> {
        try {
            val userDetails = authentication.principal as UserDetailsImpl
            logger.info(
                LOG_TAG, "receive request for recruiter add observation in interview", mapOf(
                    "user-id" to userDetails.getId().toString()
                )
            )
            val interview = interviewService.getInterview(id).filter {
                it.recruiter!!.id == userDetails.getId() ||
                        userDetails.isAdmin()
            }
                .orElseThrow()

            val interviewUpdated = updateinterview(interview, updateParcialInterviewRequest)

            interviewService.updateInterview(interviewUpdated)
            return ResponseEntity<Any>(HttpStatus.OK)
        } catch (ex: NoSuchElementException) {
            throw NoSuchElementException()
        } catch (ex: Exception) {
            when (ex) {
                is IllegalArgumentException -> {
                    throw IllegalArgumentException("error updating interview data", ex)
                }
            }
            throw Exception()
        }
    }

    private fun updateinterview(
        interview: Interview,
        updateParcialInterviewRequest: UpdateParcialInterviewRequest
    ): Interview {
        interview.managerObservation =
            updateParcialInterviewRequest.managerObservation ?: interview.managerObservation
        interview.candidateObservation =
            updateParcialInterviewRequest.candidateObservation ?: interview.candidateObservation
        interview.score = updateParcialInterviewRequest.score?.toInt() ?: interview.score
        interview.interviewStatus = updateParcialInterviewRequest.interviewStatus ?: interview.interviewStatus
        if (InterviewStatusTypes.TO_BE_SCHEDULE == updateParcialInterviewRequest.interviewStatus) {
            interview.startingAt = null
        }

        return interview
    }

    @GetMapping("/candidate")
    @PreAuthorize("hasRole('ROLE_CANDIDATE')")
    fun getAllInterviewsByCandidate(
        authentication: Authentication
    ): ResponseEntity<InterviewsByCandidateResponse> {
        try {
            val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
            val candidateId = userDetails.getId()
            logger.info(LOG_TAG, "received request from user", mapOf("user-id" to candidateId.toString()))
            val candidateInterviews = interviewService.getAllInterviewsByCandidate(candidateId)
            return ResponseEntity.ok(candidateInterviews)
        } catch (ex: Exception) {
            logger.error(LOG_TAG, ex.message, ex.stackTrace)
            throw java.lang.IllegalArgumentException()
        }
    }
}