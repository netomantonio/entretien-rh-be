package br.ufpr.tcc.entretien.backend.controller

import br.ufpr.tcc.entretien.backend.service.UserDetailsImpl
import br.ufpr.tcc.entretien.backend.service.schedule.ScheduleService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/schedules")
class ScheduleController {
    @Autowired
    lateinit var scheduleService: ScheduleService

    @PreAuthorize("hasRole('ROLE_RECRUITER') or hasRole('ROLE_CANDIDATE')")
    @GetMapping("")
    fun getAllSchedules(authentication: Authentication): ResponseEntity<*> {
        return ResponseEntity.ok<Any>(scheduleService.getAllSchedules())
    }

    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CANDIDATE')")
    @GetMapping("/period")
    fun getAllAvailableSchedulesWithinPeriod(
        @RequestParam(value = "from") @DateTimeFormat(pattern = "yyyy-MM-dd")
        from: LocalDate,
        @RequestParam(value = "to") @DateTimeFormat(pattern = "yyyy-MM-dd")
        to: LocalDate
    ): ResponseEntity<*> {
        return ResponseEntity.ok<Any>(scheduleService.getAllAvailableSchedulesWithinPeriod(from, to))
    }
}