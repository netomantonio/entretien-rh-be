package br.ufpr.tcc.entretien.backend.service

import br.ufpr.tcc.entretien.backend.common.utils.sanitizeNumbers
import br.ufpr.tcc.entretien.backend.datasource.request.CandidateSignupRequest
import br.ufpr.tcc.entretien.backend.datasource.response.DashboardResponse
import br.ufpr.tcc.entretien.backend.datasource.response.DashboardRecruiterResponse
import br.ufpr.tcc.entretien.backend.model.enums.ERole
import br.ufpr.tcc.entretien.backend.model.enums.InterviewStatusTypes
import br.ufpr.tcc.entretien.backend.model.infra.Role
import br.ufpr.tcc.entretien.backend.model.users.Candidate
import br.ufpr.tcc.entretien.backend.repository.InterviewRepository
import br.ufpr.tcc.entretien.backend.repository.RoleRepository
import br.ufpr.tcc.entretien.backend.repository.UserRepository
import br.ufpr.tcc.entretien.backend.service.interfaces.IUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CandidateService : IUserService<Candidate, CandidateSignupRequest> {

    @Autowired
    lateinit var candidateRepository: UserRepository<Candidate>

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var interviewRepository: InterviewRepository

    @Autowired
    lateinit var resumeService: ResumeService

    @Autowired
    lateinit var interviewService: InterviewService

    @Autowired
    lateinit var encoder: PasswordEncoder

    override fun existsByUsername(username: String) =
        candidateRepository.existsByUsername(username)

    override fun existsByEmail(email: String) =
        candidateRepository.existsByEmail(email)

    override fun register(user: Candidate) = candidateRepository.save(user)

    fun createNewCandidate(candidate: Candidate) {
        candidate.resume = resumeService.buildNewResume(candidate)
        val newCandidate = this.register(candidate)
        val optionalInterview = interviewRepository.findByCandidateCpfWithPendingRegistration(candidate.cpf)
        if (optionalInterview.isPresent) {
            val interview = optionalInterview.get()
            interview.candidate = newCandidate
            interview.interviewStatus = InterviewStatusTypes.TO_BE_SCHEDULE
            interviewRepository.save(interview)
        }
    }

    override fun getRole(): Role = roleRepository.findByName(ERole.ROLE_CANDIDATE)
        .orElseThrow {
            RuntimeException(
                "Error: Role is not found."
            )
        }

    override fun getDashboard(id: Long, from: LocalDate, to: LocalDate): DashboardResponse {
        val nextInterview = interviewService.getCandidateNextInterview(id)
        val lastUpdate = resumeService.getCandidateResumeLastUpdate(id)
        val thisMonthScheduledInterviews = interviewService.getCandidateInterviewsWithinPeriod(id, from, to)
        val interviewsHistory = interviewService.getCandidateInterviewHistory(id)
        val interviewsStats = interviewService.getCandidateInterviewStats(id)

        var recruiterDashboardResponse = DashboardRecruiterResponse()
        recruiterDashboardResponse.nextInterview = nextInterview?.startingAt
        recruiterDashboardResponse.lastUpdate = lastUpdate
        recruiterDashboardResponse.thisMonthScheduledInterviews = thisMonthScheduledInterviews.map { interview -> DashboardResponse.fromInterview(interview) }
        recruiterDashboardResponse.interviewsHistory = interviewsHistory.map { interview -> DashboardResponse.fromInterview(interview) }
        recruiterDashboardResponse.interviewsStats = interviewsStats

        return recruiterDashboardResponse
    }

    override fun build(signupRequest: CandidateSignupRequest): Candidate {
        val roles: MutableSet<Role> = HashSet()
        val candidateRole: Role = this.getRole()
        roles.add(candidateRole)

        val candidade = Candidate()
        candidade.cep = signupRequest.cep
        candidade.pcd = signupRequest.pcd
        candidade.socialNetworking = signupRequest.socialNetworking
        candidade.username = signupRequest.username
        candidade.password = encoder.encode(signupRequest.password)
        candidade.activated = true
        candidade.roles = roles
        candidade.firstName = signupRequest.firstName
        candidade.lastName = signupRequest.lastName
//      TODO: candidade.birthDay = candidateSignupRequest.birthDay
        candidade.cpf = signupRequest.cpf.sanitizeNumbers()
        candidade.email = signupRequest.email
        candidade.phone = signupRequest.phone.sanitizeNumbers()

        return candidade
    }

    fun getCandidateById(id: Long): Candidate = candidateRepository.findById(id)
        .orElseThrow {
            RuntimeException(
                "Error: User not found."
            )
        }

    // TODO: Review this method
    fun getAllCandidates(): Iterable<Candidate> {

        val users = candidateRepository.findAll()

        return users.filterIsInstance<Candidate>()
    }

    fun update(candidate: Candidate): Candidate {
        return candidateRepository.save(candidate)
    }

}
