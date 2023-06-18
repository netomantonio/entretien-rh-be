package br.ufpr.tcc.entretien.backend.service

import br.ufpr.tcc.entretien.backend.datasource.request.CandidateSignupRequest
import br.ufpr.tcc.entretien.backend.datasource.response.InterviewByCandidateResponse
import br.ufpr.tcc.entretien.backend.datasource.response.InterviewsByCandidateResponse
import br.ufpr.tcc.entretien.backend.model.enums.ERole
import br.ufpr.tcc.entretien.backend.model.enums.InterviewStatusTypes
import br.ufpr.tcc.entretien.backend.model.infra.Role
import br.ufpr.tcc.entretien.backend.model.interview.Interview
import br.ufpr.tcc.entretien.backend.model.users.Candidate
import br.ufpr.tcc.entretien.backend.repository.InterviewRepository
import br.ufpr.tcc.entretien.backend.repository.RoleRepository
import br.ufpr.tcc.entretien.backend.repository.UserRepository
import br.ufpr.tcc.entretien.backend.service.interfaces.IUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
        candidade.cpf = signupRequest.cpf
        candidade.email = signupRequest.email
        candidade.phone = signupRequest.phone

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

    fun getAllInterviews(candidateId: Long): InterviewsByCandidateResponse {
        val interviewsModel = interviewRepository.findAllByCandidateId(candidateId).orElseGet(null)
        return InterviewsByCandidateResponse(interviews = interviewsModel.map { it.toResponse() })
    }

    fun update(candidate: Candidate): Candidate {
        return candidateRepository.save(candidate)
    }

}

private fun Interview.toResponse(): InterviewByCandidateResponse {
    return InterviewByCandidateResponse(
        id = this.getId().toString(),
        companyName = this.manager.companyName,
        status = this.interviewStatus.name,
        appointmentDate = this.startingAt?.formatter()
    )
}
private fun LocalDateTime?.formatter(): String? {
    if (this == null) return null
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
    return this.format(formatter)

}
