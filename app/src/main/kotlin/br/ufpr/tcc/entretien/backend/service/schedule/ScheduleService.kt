package br.ufpr.tcc.entretien.backend.service.schedule

import br.ufpr.tcc.entretien.backend.common.logger.LOGGER
import br.ufpr.tcc.entretien.backend.datasource.request.RecruiterScheduleRequest
import br.ufpr.tcc.entretien.backend.model.Schedule
import br.ufpr.tcc.entretien.backend.model.enums.DaysOfTheWeek
import br.ufpr.tcc.entretien.backend.model.users.Recruiter
import br.ufpr.tcc.entretien.backend.repository.ScheduleRepository
import br.ufpr.tcc.entretien.backend.repository.UserRepository
import br.ufpr.tcc.entretien.backend.service.RecruiterService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalTime
import kotlin.jvm.Throws

@Service
class ScheduleService {

    companion object {
        val logger = LOGGER.getLogger(ScheduleService::class.java)
        private const val LOG_TAG = "entretien-rh-backend-schedule-service"
    }

    @Autowired
    lateinit var recruiterRepository: UserRepository<Recruiter>

    @Autowired
    lateinit var scheduleRepository: ScheduleRepository

    @Autowired
    lateinit var recruiterService: RecruiterService


    fun buildSchedule(
        recruiter: Recruiter,
        dayOfTheWeek: DaysOfTheWeek,
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
        logger.info(LOG_TAG, "add schedule entry init process", mapOf("RecruiterScheduleRequest" to recruiterScheduleRequest.agenda.toString() ))
        val dayOfTheWeek = recruiterScheduleRequest.agenda.dayOfTheWeek
        val periods = mapOf(
            dayOfTheWeek to
                    recruiterScheduleRequest.agenda.timesOfTheDay.map {
                        dividePeriod(
                            it.startingAt,
                            it.endingAt
                        )
                    })

        val recruiter = recruiterService.getRecruiterById(recruiterId)
        logger.info(LOG_TAG, "get recruiter successfully for id: `${recruiterId}`", mapOf("recruiter" to recruiter.firstName.toString()))
        val schedules: MutableList<Schedule> = mutableListOf()


        for (timeSpan in periods[dayOfTheWeek]!![0]) {
            var newSchedule: Schedule?
            newSchedule = Schedule(
                recruiter = recruiter,
                dayOfTheWeek = dayOfTheWeek,
                startingAt = timeSpan.first,
                endingAt = timeSpan.second
            )
            schedules.add(newSchedule)
            logger.info(LOG_TAG, "new schedules added successfully")
        }
        if (recruiter.schedule == null) {
            recruiter.schedule = mutableListOf()
        } else {
            for (schedule in schedules) {
                if (this.isOverlappingRecruiterSchedule(schedule, recruiter)) {
                    // TODO: allow to persist every other entry not overlapping
                    throw ScheduleException(ScheduleExceptionType.OVERLAPPING_SCHEDULE, "Overlapping schedule.")
                }
            }
        }

        recruiter.schedule!!.addAll(schedules)

        this.recruiterRepository.save(recruiter)

        logger.info(LOG_TAG, "all periods have been successfully saved to the recruiter")
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
            if (it.dayOfTheWeek == newSchedule.dayOfTheWeek) {
                val isOverlapping = isOverlapping(newSchedule, it)
                println(isOverlapping)
                if (isOverlapping)
                    return true
            }
        }

        return false
    }

    fun removeAllScheduleEntriesByRecruiterId(recruiterId: Long) {
        scheduleRepository.removeAllByRecruiterId(recruiterId)
    }

    fun isOverlapping(firstSchedule: Schedule, secondSchedule: Schedule): Boolean {
        val overlaps: Boolean = if (firstSchedule.startingAt.isBefore(secondSchedule.startingAt)) {
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

    fun getAllAvailableSchedulesWithinPeriod(from: LocalDate, to: LocalDate): Iterable<Schedule> {
        val toTimeString = Timestamp.valueOf(to.atStartOfDay())
        val fromTimeString = Timestamp.valueOf(from.atStartOfDay())
        val schedules = scheduleRepository.getAvailableWithinPeriod(
            fromTimeString,
            toTimeString
        )
        if (schedules.none())
            return emptyList()

        return schedules
    }

    fun getAllSchedules(): Iterable<Schedule> {
        return scheduleRepository.findAll()
    }

    fun dividePeriod(periodoInicial: LocalTime, periodoFinal: LocalTime): List<Pair<LocalTime, LocalTime>> {
        val periodos = mutableListOf<Pair<LocalTime, LocalTime>>()

        var horaAtual = periodoInicial
        while (horaAtual.isBefore(periodoFinal) || horaAtual == periodoFinal.minusHours(1)) {
            val proximaHora = horaAtual.plusHours(1)
            val periodo = Pair(horaAtual, proximaHora)
            periodos.add(periodo)
            horaAtual = proximaHora
        }

        logger.info(LOG_TAG, "period `${periodoInicial} - ${periodoFinal}` divided into `${periodos.size}` hour intervals")

        return periodos
    }

}
