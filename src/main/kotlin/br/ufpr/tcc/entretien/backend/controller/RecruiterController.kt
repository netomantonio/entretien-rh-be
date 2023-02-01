package br.ufpr.tcc.entretien.backend.controller

import br.ufpr.tcc.entretien.backend.datasource.request.RecruiterScheduleRequest
import br.ufpr.tcc.entretien.backend.datasource.request.RecruiterSignupRequest
import br.ufpr.tcc.entretien.backend.model.Schedule
import br.ufpr.tcc.entretien.backend.service.RecruiterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

// TODO: review
@RestController
@RequestMapping("/api/recruiter")
class RecruiterController {

    @Autowired
    lateinit var recruiterService: RecruiterService

    @PostMapping("")
    fun registerRecruiter(@Valid @RequestBody recruiterSignupRequest: RecruiterSignupRequest): ResponseEntity<*> {

        if (recruiterSignupRequest != null) {
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

    @PostMapping("/schedule")
    fun addScheduleEntry(@Valid @RequestBody recruiterScheduleRequest: RecruiterScheduleRequest): ResponseEntity<*> {
        return try {
            recruiterService.addScheduleEntry(recruiterScheduleRequest)
            ResponseEntity.ok<Any>("User updated successfully!")
        } catch (ex: Exception) {
            println("[ERROR] ------------------------------------------")
            println(ex.message)
            ResponseEntity.internalServerError().body("Persistence error.")
        }
    }
}