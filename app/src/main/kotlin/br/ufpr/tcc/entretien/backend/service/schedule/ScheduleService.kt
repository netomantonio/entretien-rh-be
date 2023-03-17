package br.ufpr.tcc.entretien.backend.service.schedule

import br.ufpr.tcc.entretien.backend.datasource.request.RecruiterScheduleRequest
import br.ufpr.tcc.entretien.backend.model.Schedule
import br.ufpr.tcc.entretien.backend.model.enums.EDayOfTheWeek
import br.ufpr.tcc.entretien.backend.model.users.Recruiter
import br.ufpr.tcc.entretien.backend.repository.ScheduleRepository
import br.ufpr.tcc.entretien.backend.repository.UserRepository
import br.ufpr.tcc.entretien.backend.service.RecruiterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalTime
import kotlin.jvm.Throws

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

    @Throws(ScheduleException::class)
    fun addScheduleEntry(recruiterScheduleRequest: RecruiterScheduleRequest, recruiterId: Long) {

        var recruiter = recruiterService.getRecruiterById(recruiterId)
        var schedules: MutableList<Schedule> = mutableListOf()

        for (agenda in recruiterScheduleRequest.agenda) {
            for (timeSpan in agenda.timesOfTheDay) {
                var newSchedule: Schedule? = null
                newSchedule = Schedule(
                    recruiter = recruiter,
                    dayOfTheWeek = agenda.dayOfTheWeek,
                    startingAt = timeSpan.startingAt,
                    endingAt = timeSpan.endingAt
                )
                schedules.add(newSchedule)
            }
        }
        if (recruiter.schedule == null) {
            recruiter.schedule = mutableListOf()
        } else {
            for (schedules in schedules) {
                if (this.isOverlappingRecruiterSchedule(schedules, recruiter)) {
                    // TODO: allow to persist every other entry not overlapping
                    throw ScheduleException(ScheduleExceptionType.OVERLAPPING_SCHEDULE, "Overlapping schedules.")
                }
            }
        }

        recruiter.schedule!!.addAll(schedules)

        this.recruiterRepository.save(recruiter)
    }

    fun assertScheduleOwnership(scheduleId: Long, recruiterId: Long): Boolean {
        val schedule: Schedule = scheduleRepository.findById(scheduleId).get()
        return schedule.recruiter.id == recruiterId
    }

    fun removeScheduleEntryById(scheduleId: Long) {
        scheduleRepository.deleteById(scheduleId)
    }

    fun isOverlappingRecruiterSchedule(newSchedule: Schedule, recruiter: Recruiter): Boolean {
        val schedules = scheduleRepository.getAllByRecruiterId(recruiter.id).get()

        schedules.forEach {
            println(isOverlapping(newSchedule, it))
            if (isOverlapping(newSchedule, it))
                return true
        }

        return false
    }

    fun removeAllScheduleEntriesByRecruiterId(recruiterId: Long) {
        scheduleRepository.removeAllByRecruiterId(recruiterId)
    }

    fun isOverlapping(firstSchedule: Schedule, secondSchedule: Schedule): Boolean {
        var overlaps: Boolean = if (firstSchedule.startingAt.isBefore(secondSchedule.startingAt)) {
            firstSchedule.startingAt.isAfter(secondSchedule.endingAt) ||
                    firstSchedule.endingAt.isAfter(secondSchedule.startingAt)
        } else {
            secondSchedule.startingAt.isAfter(firstSchedule.endingAt) ||
                    secondSchedule.endingAt.isAfter(firstSchedule.startingAt)
        }

        return overlaps
    }

    fun getAllSchedulesByRecruiterId(recruiterId: Long): Iterable<Schedule> =
        scheduleRepository.getAllByRecruiterId(recruiterId).get()

    fun getAllAvailableSchedules(): Iterable<Schedule> {
        val schedules = scheduleRepository.findAll()
        return schedules.distinctBy { Pair(it.startingAt, it.endingAt) }.filter { it.available }
    }

}