package br.ufpr.tcc.entretien.backend.service

import br.ufpr.tcc.entretien.backend.common.logger.LOGGER
import br.ufpr.tcc.entretien.backend.common.utils.sanitizeNumbers
import br.ufpr.tcc.entretien.backend.datasource.response.DashboardResponse
import br.ufpr.tcc.entretien.backend.datasource.response.InterviewByCandidateResponse
import br.ufpr.tcc.entretien.backend.datasource.response.InterviewsByCandidateResponse
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
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.NoSuchElementException

@Service
class InterviewService {

    companion object {
        private const val LOG_TAG = "entretien-backend-interview-service"
        private val logger = LOGGER.getLogger(InterviewService::class.java)
    }

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


    fun createInterview(candidateCpf: String, recruiterObservation: String, managerId: Long): Interview {
        val cpfSanitized = candidateCpf.sanitizeNumbers()
        val manager: Manager = this.getManagerById(managerId)

        val candidate: Candidate? = try {
            this.candidateRepository.findByCpf(cpfSanitized).get()
        } catch (e: NoSuchElementException) {
            null
        }

        val interview = Interview()
        interview.interviewStatus = InterviewStatusTypes.TO_BE_SCHEDULE
        if (candidate != null) {
            interview.candidate = candidate
        } else {
            interview.cpf = cpfSanitized
            interview.interviewStatus = InterviewStatusTypes.WAITING_CANDIDATE_REGISTRATION
        }
        interview.manager = manager

        if (recruiterObservation.isNotEmpty()) {
            interview.recruiterObservation = recruiterObservation
        }

        interview.sessionId = UUID.randomUUID().toString()

        return registerInterview(interview)
    }

    fun registerInterview(interview: Interview): Interview = interviewRepository.save(interview).also {
        logger.info(LOG_TAG, "Interview saved successfull", mapOf("interview-id" to interview.getId().toString()))
    }

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

    fun commitInterview(scheduleId: Long, interviewId: Long, date: LocalDate) {
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

        interviewRepository.save(interview)
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

    fun updateInterview(interview: Interview): Interview = interviewRepository.save(interview).also {
        logger.info(LOG_TAG, "Interview update successfull", mapOf("interview-id" to interview.getId().toString()))
    }

    fun findBySessionId(sessionId: String): Interview = interviewRepository.findBySessionId(sessionId).get().also {
        logger.info(
            LOG_TAG,
            "interview found for the sessionId",
            mapOf("session-id" to sessionId, "interview-id" to it.getId().toString())
        )
    }

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

    fun getCandidateInterviewsWithinPeriod(id: Long, from: LocalDate, to: LocalDate): Iterable<Interview> {
        val interviews = interviewRepository.getWithinPeriodByCandidate(
            id,
            from.atStartOfDay(),
            to.atStartOfDay()
        )
        if (interviews.none())
            return emptyList()

        return interviews
    }

    fun getCandidateNextInterview(candidateId: Long): Interview? {
        val today = LocalDateTime.now()
        return if(interviewRepository.getCandidateNextInterview(candidateId, today).isEmpty)
            null
        else interviewRepository.getCandidateNextInterview(candidateId, today).get()
    }

    fun getRecruiterNextInterview(recruiterId: Long): Interview? {
        val today = LocalDateTime.now()
        val optional = interviewRepository.getRecruiterNextInterview(recruiterId, today)
        return if (optional.isEmpty) {
            return null
        } else {
            optional.get()
        }
    }

    fun getRecruiterInterviewsWithinPeriod(id: Long, from: LocalDate, to: LocalDate): Iterable<Interview> {
        val interviews = interviewRepository.getWithinPeriodByRecruiter(
            id,
            from.atStartOfDay(),
            to.atStartOfDay()
        )
        if (interviews.none())
            return emptyList()

        return interviews
    }

    fun getRecruiterInterviewHistory(id: Long): Iterable<Interview> {
        return interviewRepository.getRecruiterConcludedInterviews(id)
    }

    fun getRecruiterInterviewStats(id: Long): DashboardResponse.InterviewsStats {
        var scheduledQtd = interviewRepository.getRecruiterQtdByStatus(id, InterviewStatusTypes.SCHEDULE.name)
        var toBeScheduledQtd = interviewRepository.getRecruiterQtdByStatus(id, InterviewStatusTypes.TO_BE_SCHEDULE.name)
        var concluded = interviewRepository.getRecruiterQtdByStatus(id, InterviewStatusTypes.CONCLUDED.name)
        var total = interviewRepository.getRecruiterTotalInterviewsQtd(id)

        return DashboardResponse.InterviewsStats(scheduledQtd, toBeScheduledQtd, concluded, total)
    }

    fun getCandidateInterviewHistory(id: Long): Iterable<Interview> =
        interviewRepository.getCandidateConcludedInterviews(id)

    fun getCandidateInterviewStats(id: Long): DashboardResponse.InterviewsStats {
        var scheduledQtd = interviewRepository.getCandidateQtdByStatus(id, InterviewStatusTypes.SCHEDULE.toString())
        var toBeScheduledQtd = interviewRepository.getCandidateQtdByStatus(id, InterviewStatusTypes.TO_BE_SCHEDULE.toString())
        var concluded = interviewRepository.getCandidateQtdByStatus(id, InterviewStatusTypes.CONCLUDED.toString())
        var total = interviewRepository.getCandidateTotalInterviewsQtd(id)

        return DashboardResponse.InterviewsStats(scheduledQtd, toBeScheduledQtd, concluded, total)
    }

    fun getInterviewStats(): DashboardResponse.InterviewsStats {
        var scheduledQtd = interviewRepository.getQtdByStatus(InterviewStatusTypes.SCHEDULE)
        var toBeScheduledQtd = interviewRepository.getQtdByStatus(InterviewStatusTypes.TO_BE_SCHEDULE)
        var concluded = interviewRepository.getQtdByStatus(InterviewStatusTypes.CONCLUDED)
        var total = interviewRepository.getTotalQtd()

        return DashboardResponse.InterviewsStats(scheduledQtd, toBeScheduledQtd, concluded, total)
    }

    fun getAllInterviewsByStatusWithinPeriod(status: InterviewStatusTypes, from: LocalDate, to: LocalDate): List<Interview> {
        return interviewRepository.findAllScheduleWithinPeriod(from.atStartOfDay(), to.atStartOfDay())
    }

    fun getInterviewProblemHistory(): List<Interview> {
        val absentCandidate = interviewRepository.findAllByStatus(InterviewStatusTypes.ABSENT_CANDIDATE)
        val absentRecruiter = interviewRepository.findAllByStatus(InterviewStatusTypes.ABSENT_RECRUITER)
        val didNotOccur = interviewRepository.findAllByStatus(InterviewStatusTypes.DID_NOT_OCCUR)
        var problemHistory = mutableListOf<Interview>()
        problemHistory.addAll(absentCandidate)
        problemHistory.addAll(absentRecruiter)
        problemHistory.addAll(didNotOccur)

        return Collections.unmodifiableList(problemHistory)
    }

    fun getUnregisteredCpfQtd(): Long = interviewRepository.getQtdByStatus(InterviewStatusTypes.WAITING_CANDIDATE_REGISTRATION)

    fun getInterviewsWithinPeriod(from: LocalDate, to: LocalDate): List<Interview> {
        return interviewRepository.findAllScheduleWithinPeriod(from.atStartOfDay(), to.atStartOfDay())
    }

    fun getLastByManager(id: Long): Interview? {
        val interviews = interviewRepository.getManagerLastInterview(id)
        return interviews.firstOrNull()
    }

    fun getManagerInterviewsWithinPeriod(id: Long, from: LocalDate, to: LocalDate): Iterable<Interview> {
        val interviews = interviewRepository.getWithinPeriodByManager(
            id,
            from.atStartOfDay(),
            to.atStartOfDay()
        )
        if (interviews.none())
            return emptyList()

        return interviews
    }

    fun getManagerInterviewHistory(id: Long): Iterable<Interview> =
        interviewRepository.getManagerConcludedInterviews(id)

    fun getManagerInterviewStats(id: Long): DashboardResponse.InterviewsStats {
        var scheduledQtd = interviewRepository.getManagerQtdByStatus(id, InterviewStatusTypes.SCHEDULE.toString())
        var toBeScheduledQtd = interviewRepository.getManagerQtdByStatus(id, InterviewStatusTypes.TO_BE_SCHEDULE.toString())
        var concluded = interviewRepository.getManagerQtdByStatus(id, InterviewStatusTypes.CONCLUDED.toString())
        var total = interviewRepository.getManagerTotalInterviewsQtd(id)

        return DashboardResponse.InterviewsStats(scheduledQtd, toBeScheduledQtd, concluded, total)
    }

    fun setUserPresent(
        userDetails: UserDetailsImpl,
        interview: Interview
    ) {
        if (userDetails.getId() == interview.candidate!!.id) interview.candidatePresent = true
        if (userDetails.getId() == interview.recruiter!!.id) interview.recruiterPresent = true
        interviewRepository.save(interview)
    }

    fun getAllInterviewsByCandidate(candidateId: Long): InterviewsByCandidateResponse {
        val interviewsModel = interviewRepository.findAllByCandidateId(candidateId).orElseGet(null)
        return InterviewsByCandidateResponse(interviews = interviewsModel.map { it.toResponse() })
    }

    fun saveAll(interviews: List<Interview>) {
        interviewRepository.saveAll(interviews)
    }

}
private fun Interview.toResponse(): InterviewByCandidateResponse {
    return InterviewByCandidateResponse(
        id = this.getId().toString(),
        companyName = this.manager.companyName,
        status = this.interviewStatus.name,
        appointmentDate = this.startingAt?.formatter(),
        sessionId = this.sessionId
    )
}

private fun LocalDateTime?.formatter(): String? {
    if (this == null) return null
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    return this.format(formatter)

}