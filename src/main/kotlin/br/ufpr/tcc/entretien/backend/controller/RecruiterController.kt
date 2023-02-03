package br.ufpr.tcc.entretien.backend.controller

import br.ufpr.tcc.entretien.backend.datasource.request.RecruiterScheduleRequest
import br.ufpr.tcc.entretien.backend.datasource.request.RecruiterSignupRequest
import br.ufpr.tcc.entretien.backend.model.Schedule
import br.ufpr.tcc.entretien.backend.model.users.Recruiter
import br.ufpr.tcc.entretien.backend.service.RecruiterService
import br.ufpr.tcc.entretien.backend.service.ScheduleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/api/recruiters")
class RecruiterController {

    @Autowired
    lateinit var recruiterService: RecruiterService

    @Autowired
    lateinit var scheduleService: ScheduleService

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

    @GetMapping
    fun getAllRecruiters(): Iterable<Recruiter> = recruiterService.getAllRecruiters()

    @GetMapping("/{id}")
    fun getRecruiterById(@PathVariable id: Long): Recruiter = recruiterService.getRecruiterById(id)

    @PreAuthorize(
        "hasRole('ROLE_ADMIN') or hasRole('ROLE_RECRUITER') and #recruiterScheduleRequest.recruiterId == principal.id")
    @PostMapping("/schedules")
    fun addAvailableSchedule(@Valid @RequestBody recruiterScheduleRequest: RecruiterScheduleRequest): ResponseEntity<*> {
        if (!recruiterService.existsById(recruiterScheduleRequest.recruiterId)) {
            return ResponseEntity
                .badRequest()
                .body<Any>(("Error: Invalid id for Recruiter"))
        }
        return try {
            scheduleService.addScheduleEntry(recruiterScheduleRequest)
            ResponseEntity.ok<Any>("Schedule added successfully!")
        } catch (ex: Exception) {
            println("[ERROR] ------------------------------------------")
            println(ex.message)
            ResponseEntity.internalServerError().body("Persistence error.")
        }
    }

    @PreAuthorize(
        "hasRole('ROLE_ADMIN') or hasRole('ROLE_RECRUITER') and #id == principal.id")
    @GetMapping("/schedules/{id}")
    fun getAllAvailableScheduleByRecruiterId(@PathVariable id: Long): ResponseEntity<*>{
        if (!recruiterService.existsById(id)) {
            return ResponseEntity
                .badRequest()
                .body<Any>(("Error: Invalid id for Recruiter"))
        }
        return try {
            var schedules: Iterable<Schedule> = scheduleService.getAllSchedulesByRecruiterId(id)
            ResponseEntity.ok<Any>(schedules)
        } catch (ex: Exception) {
            println("[ERROR] ------------------------------------------")
            println(ex.message)
            ResponseEntity.internalServerError().body("Error?")
        }
    }
}