package br.ufpr.tcc.entretien.backend.controller

import br.ufpr.tcc.entretien.backend.datasource.request.InterviewRequest
import br.ufpr.tcc.entretien.backend.service.InterviewService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
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
    fun registerInterview(@Valid @RequestBody interviewRequest: InterviewRequest): ResponseEntity<*> {
        return try {
            interviewService.createInterview(interviewRequest)
            ResponseEntity.ok<Any>("Interview registered successfully!")
        } catch (ex: Exception) {
            println("[ERROR] ------------------------------------------")
            println(ex.message)
            ex.printStackTrace()
            ResponseEntity.internalServerError().body("Persistence error.")
        }
    }
}