package br.ufpr.tcc.entretien.backend.service

import br.ufpr.tcc.entretien.backend.datasource.request.InterviewRequest
import br.ufpr.tcc.entretien.backend.model.Schedule
import br.ufpr.tcc.entretien.backend.model.enums.InterviewStatusTypes
import br.ufpr.tcc.entretien.backend.model.interview.Interview
import br.ufpr.tcc.entretien.backend.model.users.Candidate
import br.ufpr.tcc.entretien.backend.model.users.Manager
import br.ufpr.tcc.entretien.backend.model.users.Recruiter
import br.ufpr.tcc.entretien.backend.repository.InterviewRepository
import br.ufpr.tcc.entretien.backend.repository.ScheduleRepository
import br.ufpr.tcc.entretien.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Optional

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
    lateinit var scheduleRepository: ScheduleRepository


    fun createInterview(candidateCpf: String, managerObservation: String, managerId: Long) {

        val manager: Manager = this.getManagerById(managerId)

        var candidate: Candidate? = try {
            this.candidateRepository.findByCpf(candidateCpf).get()
        } catch (e: NoSuchElementException) {
            null
        }

        var interview = Interview()
        interview.interviewStatus = InterviewStatusTypes.TO_BE_SCHEDULE
        if (candidate != null) {
            interview.candidate = candidate
        } else {
            interview.cpf = candidateCpf
            interview.interviewStatus = InterviewStatusTypes.WAITING_CANDIDATE_REGISTRATION
        }
        interview.manager = manager

        if (managerObservation.isNotEmpty()) {
            interview.managerObservation = managerObservation
        }

        this.registerInterview(interview)
    }

    fun registerInterview(interview: Interview) = interviewRepository.save(interview)

    fun getInterview(id: Long): Optional<Interview> = interviewRepository.findById(id)

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

    fun isAvailableForCandidate(candidateId: Long): Boolean {
        return interviewRepository.existsByCandidateId(candidateId)
    }

    fun isInterviewRelated(id: Long, interview: Interview): Boolean {
        return (interview.manager.id == id || interview.candidate?.id == id || interview.recruiter?.id == id)
    }

    fun canDelete(interview: Interview): Boolean {
        return (interview.interviewStatus == InterviewStatusTypes.TO_BE_SCHEDULE
                || interview.interviewStatus == InterviewStatusTypes.WAITING_CANDIDATE_REGISTRATION)
    }

    fun commitInterview(scheduleId: Long, date: LocalDate, candidateId: Long) {
        if (!interviewRepository.existsByCandidateId(candidateId)) {
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
        interview.recruiter = recruiter
        interview.interviewStatus = InterviewStatusTypes.SCHEDULE

        interviewRepository.save(interview)
    }

    fun getScheduleInterviewsByRecruiter(recruiterId: Long): Iterable<Interview> {
        return interviewRepository.findByRecruiterId(recruiterId).get()
    }

    fun getAllByManager(managerId: Long): Iterable<Interview> {
        return interviewRepository.findByManagerId(managerId).get()
    }

    fun updateInterview(interview: Interview): Interview = interviewRepository.save(interview)

    fun adjustInterview(interview: Interview, candidateCpf: String?, managerObservation: String?): Interview {
        if(managerObservation?.isNotEmpty() == true)
            interview.managerObservation = managerObservation
        if(candidateCpf?.isNotEmpty() == true)
            interview.cpf = candidateCpf
        return interviewRepository.save(interview)
    }

    fun deleteInterview(interview: Interview) {
        interviewRepository.delete(interview)
    }
}
