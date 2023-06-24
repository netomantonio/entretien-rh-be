package br.ufpr.tcc.entretien.backend.controller

import br.ufpr.tcc.entretien.backend.common.logger.LOGGER
import br.ufpr.tcc.entretien.backend.datasource.request.RecruiterScheduleRequest
import br.ufpr.tcc.entretien.backend.datasource.request.RecruiterSignupRequest
import br.ufpr.tcc.entretien.backend.datasource.request.RecruiterUpdateModelRequest
import br.ufpr.tcc.entretien.backend.datasource.response.ScheduleResponse
import br.ufpr.tcc.entretien.backend.datasource.response.toResponse
import br.ufpr.tcc.entretien.backend.model.Schedule
import br.ufpr.tcc.entretien.backend.model.users.Recruiter
import br.ufpr.tcc.entretien.backend.service.InterviewService
import br.ufpr.tcc.entretien.backend.service.RecruiterService
import br.ufpr.tcc.entretien.backend.service.UserDetailsImpl
import br.ufpr.tcc.entretien.backend.service.schedule.ScheduleException
import br.ufpr.tcc.entretien.backend.service.schedule.ScheduleService
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
@RequestMapping("/api/recruiters")
class RecruiterController {
    companion object {
        private const val LOG_TAG = "entretien-backend-recruiter-controller"
        private val logger = LOGGER.getLogger(RecruiterController::class.java)
    }

    @Autowired
    lateinit var recruiterService: RecruiterService

    @Autowired
    lateinit var scheduleService: ScheduleService

    @Autowired
    lateinit var interviewService: InterviewService


    @PostMapping
    fun registerRecruiter(@Valid @RequestBody recruiterSignupRequest: RecruiterSignupRequest): ResponseEntity<*> {

        if (recruiterService.existsByUsername(recruiterSignupRequest.username)) {
            return ResponseEntity
                .badRequest()
                .body<Any>(("Error: Username is already taken!"))
        }
        if (recruiterService.existsByEmail(recruiterSignupRequest.email)) {
            return ResponseEntity
                .badRequest()
                .body<Any>("Error: Email is already in use!")
        }

        val recruiter = recruiterService.build(recruiterSignupRequest)

        return try {
            recruiterService.register(recruiter)
            ResponseEntity.ok<Any>("User registered successfully!")
        } catch (ex: Exception) {
            println("[ERROR] ------------------------------------------")
            println(ex.message)
            ResponseEntity.internalServerError().body("Persistence error.")
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    fun getAllRecruiters(): Iterable<Recruiter> = recruiterService.getAllRecruiters()

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECRUITER') and #id == principal.id")
    @GetMapping("/{id}")
    fun getRecruiterById(@PathVariable id: Long): Recruiter = recruiterService.getRecruiterById(id)

    @PreAuthorize("hasRole('ROLE_RECRUITER')")
    @PostMapping("/schedules")
    fun addAvailableSchedule(
        @Valid @RequestBody recruiterScheduleRequest: RecruiterScheduleRequest,
        authentication: Authentication
    ): ResponseEntity<*> {
        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val recruiterId = userDetails.getId()
        if (!recruiterService.existsById(recruiterId)) {
            return ResponseEntity
                .badRequest()
                .body<Any>(("Error: Invalid id for Recruiter"))
        }
        return try {
            scheduleService.addScheduleEntry(recruiterScheduleRequest, recruiterId)
            ResponseEntity.ok<Any>("Schedule added successfully!")
        } catch (ex: ScheduleException) {
            println("[ERROR] ------------------------------------------")
            println(ex.type.name)
            ResponseEntity.badRequest().body("Overlapping schedule.")
        } catch (ex: Exception) {
            println("[ERROR] ------------------------------------------")
            println(ex.message)
            ResponseEntity.internalServerError().body("Persistence error.")
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECRUITER')")
    @GetMapping("/schedules/{id}")
    fun getAllAvailableScheduleByRecruiterId(@PathVariable id: Long): ResponseEntity<*> {
        if (!recruiterService.existsById(id)) {
            return ResponseEntity
                .badRequest()
                .body<Any>(("Error: Invalid id for Recruiter"))
        }
        return try {
            val schedules: Iterable<Schedule> = scheduleService.getAllSchedulesByRecruiterId(id)
            val schedulesResponse = toMappingResponse(schedules)
            ResponseEntity.ok<Any>(schedulesResponse)
        } catch (ex: Exception) {
            println("[ERROR] ------------------------------------------")
            println(ex.message)
            ResponseEntity.internalServerError().body("Error?")
        }
    }

    private fun toMappingResponse(schedules: Iterable<Schedule>): List<ScheduleResponse>? {
        return schedules.map { schedule -> schedule.toResponse() }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECRUITER')")
    @DeleteMapping("/schedules/{id}")
    fun removeAvailableScheduleById(@PathVariable id: Long, authentication: Authentication): ResponseEntity<*> {

        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl

        val recruiterId = userDetails.getId()

        println("isAdmin: " + userDetails.isAdmin())

        if ((!scheduleService.assertScheduleOwnership(id, recruiterId))) {
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body<Any>(("Logged user can only remove it´s own schedules."))
        }

        try {
            scheduleService.removeScheduleEntryById(id)
        } catch (ex: Exception) {
            println("[ERROR] ------------------------------------------")
            println(ex.message)
            ResponseEntity.internalServerError().body("Error?")
        }

        return ResponseEntity
            .ok()
            .body<Any>("Schedule removed")
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_RECRUITER')")
    @DeleteMapping("/schedules")
    fun removeAllAvailableSchedulesByRecruiterId(authentication: Authentication): ResponseEntity<*> {

        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl

        val recruiterId = userDetails.getId()
        try {
            scheduleService.removeAllScheduleEntriesByRecruiterId(recruiterId)
        } catch (ex: Exception) {
            println("[ERROR] ------------------------------------------")
            println(ex.message)
            ResponseEntity.internalServerError().body("Error?")
        }

        return ResponseEntity
            .ok()
            .body<Any>("Schedules removed.")
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CANDIDATE')")
    @GetMapping("/schedules")
    fun getAllAvailableSchedules(): ResponseEntity<*> {
        return try {
            ResponseEntity
                .ok()
                .body<Any>(scheduleService.getAllAvailableSchedules())
        } catch (ex: Exception) {
            println("[ERROR] ------------------------------------------")
            println(ex.message)
            ResponseEntity.internalServerError().body("Error?")
        }
    }

    @PreAuthorize("hasRole('ROLE_RECRUITER')")
    @GetMapping("/interviews")
    fun getAllRecruiterScheduledInterviews(authentication: Authentication): ResponseEntity<*> {

        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val recruiterId = userDetails.getId()

        return try {
            ResponseEntity
                .ok()
                .body<Any>(interviewService.getScheduleInterviewsByRecruiter(recruiterId))
        } catch (ex: Exception) {
            println("[ERROR] ------------------------------------------")
            println(ex.message)
            ResponseEntity.internalServerError().body("Error?")
        }
    }

    @PreAuthorize("hasRole('ROLE_RECRUITER')")
    @GetMapping("/dashboard")
    fun getAllRecruiterDashboard(
        @RequestParam(value = "from") @DateTimeFormat(pattern = "yyyy-MM-dd")
        from: LocalDate,
        @RequestParam(value = "to") @DateTimeFormat(pattern = "yyyy-MM-dd")
        to: LocalDate,
        authentication: Authentication
    ): ResponseEntity<*> {
        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val recruiterId = userDetails.getId()

        return ResponseEntity.ok(recruiterService.getDashboard(recruiterId, from, to))
    }

    @PreAuthorize("hasRole('ROLE_RECRUITER')")
    @PutMapping
    fun updated(
        @Valid @RequestBody recruiterUpdateRequest: RecruiterUpdateModelRequest,
        authentication: Authentication
    ): ResponseEntity<String?>? {
        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val recruiterId = userDetails.getId()
        logger.info(LOG_TAG, "receive request update recruiter user", mapOf("user-id" to recruiterId.toString()))
        try {
            val recruiterUpdated = recruiterUpdateRequest.toModel(recruiterService.getRecruiterById(recruiterId))
            recruiterService.update(recruiterUpdated)
            return ResponseEntity(HttpStatus.OK)
        } catch (ex: Exception) {
            throw Exception()
        }
    }
    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_RECRUITER')")
    fun getMe(
        authentication: Authentication
    ): ResponseEntity<Recruiter> {
        logger.info(LOG_TAG, "getMe")
        try {
            val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
            val recruiterId = userDetails.getId()
            logger.info(LOG_TAG, "received request from user", mapOf("user-id" to recruiterId.toString()))
            val me = recruiterService.getRecruiterById(recruiterId)
            return ResponseEntity.ok(me)
        } catch (ex: Exception) {
            logger.error(LOG_TAG, ex.message, ex.stackTrace)
            throw IllegalArgumentException()
        }
    }
    
}
