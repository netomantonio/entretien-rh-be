package br.ufpr.tcc.entretien.backend.service

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
import org.springframework.stereotype.Service
import java.sql.Timestamp
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


    fun createInterview(candidateCpf: String, managerObservation: String, managerId: Long): Interview {

        val manager: Manager = this.getManagerById(managerId)

        val candidate: Candidate? = try {
            this.candidateRepository.findByCpf(candidateCpf).get()
        } catch (e: NoSuchElementException) {
            null
        }

        val interview = Interview()
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

        return registerInterview(interview)
    }

    fun registerInterview(interview: Interview): Interview = interviewRepository.save(interview)

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

    fun commitInterview(scheduleId: Long, interviewId: Long, date: LocalDate): Interview {
        val interview: Interview = interviewRepository.findById(interviewId).get()
        if (interview.candidate == null) {
            throw Exception("Não autorizado.")
        }
        val schedule: Schedule = scheduleRepository.findById(scheduleId).get()
        val recruiter = recruiterRepository.findById(schedule.recruiter.id).get()

        interview.schedule = schedule
        interview.recruiter = recruiter
        interview.startingAt = LocalDateTime.of(date, schedule.startingAt)
        interview.interviewStatus = InterviewStatusTypes.SCHEDULE

        return interviewRepository.save(interview)
    }

    fun getScheduleInterviewsByRecruiter(recruiterId: Long): Iterable<Interview> {
        return interviewRepository.findByRecruiterId(recruiterId).get()
    }

    //TODO("Validar o uso desse método faz o mesmo que o método da schedule service faz? 'ScheduleService.getAllAvailableSchedulesWithinPeriod'")
    fun getFullScheduleInterviewsByPeriod(from: LocalDate, to: LocalDate): Iterable<Interview> {
        return interviewRepository.findByPeriod(
            Timestamp.valueOf(to.atStartOfDay()),
            Timestamp.valueOf(from.atStartOfDay())
        ).get()
    }

    fun getAllByManager(managerId: Long): Iterable<Interview> {
        return interviewRepository.findByManagerId(managerId).get()
    }

    fun updateInterview(interview: Interview): Interview = interviewRepository.save(interview)

    fun adjustInterview(interview: Interview, candidateCpf: String?, managerObservation: String?): Interview {
        if (managerObservation?.isNotEmpty() == true)
            interview.managerObservation = managerObservation
        if (candidateCpf?.isNotEmpty() == true)
            interview.cpf = candidateCpf
        return interviewRepository.save(interview)
    }

    fun deleteInterview(interview: Interview) {
        interviewRepository.delete(interview)
    }
}
