package br.ufpr.tcc.entretien.backend.service

import br.ufpr.tcc.entretien.backend.datasource.request.InterviewRequest
import br.ufpr.tcc.entretien.backend.model.Schedule
import br.ufpr.tcc.entretien.backend.model.interview.EInterviewStatus
import br.ufpr.tcc.entretien.backend.model.interview.Interview
import br.ufpr.tcc.entretien.backend.model.interview.InterviewStatus
import br.ufpr.tcc.entretien.backend.model.users.Candidate
import br.ufpr.tcc.entretien.backend.model.users.Manager
import br.ufpr.tcc.entretien.backend.model.users.Recruiter
import br.ufpr.tcc.entretien.backend.repository.InterviewRepository
import br.ufpr.tcc.entretien.backend.repository.InterviewStatusRepository
import br.ufpr.tcc.entretien.backend.repository.ScheduleRepository
import br.ufpr.tcc.entretien.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class InterviewService {

    @Autowired
    lateinit var candidateRepository: UserRepository<Candidate>

    @Autowired
    lateinit var recruiterRepository: UserRepository<Recruiter>

    @Autowired
    lateinit var managerRepository: UserRepository<Manager>

    @Autowired
    lateinit var interviewRepository: InterviewRepository

    @Autowired
    lateinit var interviewStatusRepository: InterviewStatusRepository

    @Autowired
    lateinit var scheduleRepository: ScheduleRepository


    fun createInterview(interviewRequest: InterviewRequest, managerId: Long) {

        val candidate: Candidate = this.getCandidateById(interviewRequest.candidateId)
        val manager: Manager = this.getManagerById(managerId)

        var interview = Interview()
        interview.interviewStatus = this.getInterviewStatus("Schedule")
        interview.candidate = candidate
        interview.manager = manager

        if (interviewRequest.managerObservation.isNotEmpty()) {
            interview.managerObservation = interviewRequest.managerObservation
        }

        interviewRepository.save(interview)
    }

    fun getAll(): Iterable<Interview> {
        return interviewRepository.findAll()
    }

    fun getManagerById(id: Long): Manager = managerRepository.findById(id)
        .orElseThrow {
            RuntimeException(
                "Error: User not found."
            )
        }

    fun getCandidateById(id: Long): Candidate = candidateRepository.findById(id)
        .orElseThrow {
            RuntimeException(
                "Error: User not found."
            )
        }

    fun getRecruiterById(id: Long): Recruiter = recruiterRepository.findById(id)
        .orElseThrow {
            RuntimeException(
                "Error: User not found."
            )
        }

    private fun getInterviewStatus(interviewStatus: String): InterviewStatus {
        if (interviewStatus == null) {
            throw (RuntimeException("Error: invalid input."))
        } else {
            when (interviewStatus) {
                "Waiting candidate" -> {
                    return interviewStatusRepository.findByName(EInterviewStatus.WAITING_CANDIDATE)
                        .orElseThrow { RuntimeException("Error: Interview Status is not found.") }
                }

                "Schedule" -> {
                    return interviewStatusRepository.findByName(EInterviewStatus.SCHEDULE)
                        .orElseThrow { RuntimeException("Error: Interview Status is not found.") }
                }

                "Absent candidate" -> {
                    return interviewStatusRepository.findByName(EInterviewStatus.ABSENT_CANDIDATE)
                        .orElseThrow { RuntimeException("Error: Interview Status is not found.") }
                }

                "Absent recruiter" -> {
                    return interviewStatusRepository.findByName(EInterviewStatus.ABSENT_RECRUITER)
                        .orElseThrow { RuntimeException("Error: Interview Status is not found.") }
                }

                "Concluded" -> {
                    return interviewStatusRepository.findByName(EInterviewStatus.CONCLUDED)
                        .orElseThrow { RuntimeException("Error: Interview Status is not found.") }
                }

                "Did not occur" -> {
                    return interviewStatusRepository.findByName(EInterviewStatus.DID_NOT_OCCUR)
                        .orElseThrow { RuntimeException("Error: Interview Status is not found.") }
                }

                "In progress" -> {
                    return interviewStatusRepository.findByName(EInterviewStatus.IN_PROGRESS)
                        .orElseThrow { RuntimeException("Error: Interview Status is not found.") }
                }

                "Other" -> {
                    return interviewStatusRepository.findByName(EInterviewStatus.OTHER)
                        .orElseThrow { RuntimeException("Error: Interview Status is not found.") }
                }

                else -> throw RuntimeException("Interview status was not found")
            }
        }
    }

    fun isAvailableForCandidate(candidateId: Long) : Boolean {
        return interviewRepository.existsByCandidateId(candidateId)
    }

    fun commitInterview(scheduleId: Long, date: LocalDate, candidateId: Long) {
        if(!interviewRepository.existsByCandidateId(candidateId)){
            // TODO: throw error (interview not available)
        }

        var schedule: Schedule = scheduleRepository.findById(scheduleId).get()
        schedule.available = false
        scheduleRepository.save(schedule)

        var interviewStartingAt = LocalDateTime.of(date, schedule.startingAt)
        interviewStartingAt.plusHours(schedule.startingAt.hour.toLong())
        interviewStartingAt.plusMinutes(schedule.startingAt.minute.toLong())

        var interview = interviewRepository.findByCandidateId(candidateId).get()
        var recruiter = recruiterRepository.findById(schedule.recruiter.id).get()
        interview.startingAt = interviewStartingAt
//        interview.endingAt = schedule.endingAt
        interview.recruiter = recruiter
        interview.interviewStatus = getInterviewStatus("Schedule")

        interviewRepository.save(interview)
    }

}
