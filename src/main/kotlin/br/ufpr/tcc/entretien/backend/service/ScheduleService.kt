package br.ufpr.tcc.entretien.backend.service

import br.ufpr.tcc.entretien.backend.datasource.request.RecruiterScheduleRequest
import br.ufpr.tcc.entretien.backend.model.Schedule
import br.ufpr.tcc.entretien.backend.model.enums.EDayOfTheWeek
import br.ufpr.tcc.entretien.backend.model.users.Recruiter
import br.ufpr.tcc.entretien.backend.repository.ScheduleRepository
import br.ufpr.tcc.entretien.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalTime
//import kotlin.jvm.Throws

@Service
class ScheduleService {

    @Autowired
    lateinit var recruiterRepository: UserRepository<Recruiter>

    @Autowired
    lateinit var scheduleRepository: ScheduleRepository

    @Autowired
    lateinit var recruiterService: RecruiterService

    fun buildSchedule(
        recruiter: Recruiter,
        dayOfTheWeek: EDayOfTheWeek,
        startingAt: LocalTime,
        endingAt: LocalTime
    ): Schedule {
        return Schedule(
            recruiter = recruiter,
            dayOfTheWeek = dayOfTheWeek,
            startingAt = startingAt,
            endingAt = endingAt
        )
    }

    // @Throws()
    fun addScheduleEntry(recruiterScheduleRequest: RecruiterScheduleRequest) {

        var recruiter = recruiterService.getRecruiterById(recruiterScheduleRequest.recruiterId)

        val newSchedule = this.buildSchedule(
            recruiter,
            recruiterScheduleRequest.dayOfTheWeek,
            recruiterScheduleRequest.startingAt,
            recruiterScheduleRequest.endingAt
        )

        if (recruiter.schedule == null) {
            recruiter.schedule = mutableSetOf()
        } else {
            if (this.isOverlappingSchedule(newSchedule, recruiter)) {
                // TODO: throw exception
                return
            }
        }

        recruiter.schedule!!.add(newSchedule)

        this.recruiterRepository.save(recruiter)
    }

    // TODO: how is this necessary?
    fun isOverlappingSchedule(newSchedule: Schedule, recruiter: Recruiter): Boolean {
        return false
    }

    fun getAllSchedulesByRecruiterId(recruiterId: Long): Iterable<Schedule> = scheduleRepository.getAllByRecruiterId(recruiterId).get()

}