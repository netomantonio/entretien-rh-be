package br.ufpr.tcc.entretien.backend.service

import br.ufpr.tcc.entretien.backend.datasource.request.CandidateSignupRequest
import br.ufpr.tcc.entretien.backend.model.*
import br.ufpr.tcc.entretien.backend.model.enums.EEducationLevel
import br.ufpr.tcc.entretien.backend.model.enums.EInterviewStatus
import br.ufpr.tcc.entretien.backend.model.enums.ERole
import br.ufpr.tcc.entretien.backend.model.infra.Role
import br.ufpr.tcc.entretien.backend.model.users.Candidate
import br.ufpr.tcc.entretien.backend.repository.EducationLevelRepository
import br.ufpr.tcc.entretien.backend.repository.RoleRepository
import br.ufpr.tcc.entretien.backend.repository.UserRepository
import br.ufpr.tcc.entretien.backend.service.interfaces.IUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class CandidateService : IUserService<Candidate, CandidateSignupRequest> {

    @Autowired
    lateinit var candidateRepository: UserRepository<Candidate>

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var encoder: PasswordEncoder

    override fun existsByUsername(username: String) =
        candidateRepository.existsByUsername(username)

    override fun existsByEmail(email: String) =
        candidateRepository.existsByEmail(email)

    private fun getEducationLevel(educationLevel: String): EducationLevel {
        if (educationLevel == null) {
            throw (RuntimeException("Error: invalid input."))
        } else {
            when (educationLevel) {
                "Ensino Fundamental" -> {
                    return educationLevelRepository.findByName(EEducationLevel.ENSINO_FUNDAMENTAL)
                        .orElseThrow { RuntimeException("Error: Education level is not found.") }
                }

                "Ensino Medio" -> {
                    return educationLevelRepository.findByName(EEducationLevel.ENSINO_MEDIO)
                        .orElseThrow { RuntimeException("Error: Education level is not found.") }
                }

                "Graduação" -> {
                    return educationLevelRepository.findByName(EEducationLevel.GRADUACAO)
                        .orElseThrow { RuntimeException("Error: Education level is not found.") }
                }

                "Ensino Medio" -> {
                    return educationLevelRepository.findByName(EEducationLevel.POS_GRADUACAO)
                        .orElseThrow { RuntimeException("Error: Education level is not found.") }
                }

                "Mestrado" -> {
                    return educationLevelRepository.findByName(EEducationLevel.MESTRADO)
                        .orElseThrow { RuntimeException("Error: Education level is not found.") }
                }

                "Doutorado" -> {
                    return educationLevelRepository.findByName(EEducationLevel.DOUTORADO)
                        .orElseThrow { RuntimeException("Error: Education level is not found.") }
                }

                else -> throw RuntimeException("User was not created")
            }
        }
    }

    override fun register(candidate: Candidate) = candidateRepository.save(candidate)

    fun buildResume(
        presentation: String,
        educationLevel: String,
        professionalHistory: MutableSet<String>,
        languages: MutableSet<String>,
        desiredJobTitle: String,
        candidate: Candidate
    ): Resume {
        val educationLevel = EEducationLevel.valueOf(educationLevel)
        return Resume(presentation, educationLevel, professionalHistory, languages, desiredJobTitle, candidate)
    }

    override fun getRole(): Role = roleRepository.findByName(ERole.ROLE_CANDIDATE)
        .orElseThrow {
            RuntimeException(
                "Error: Role is not found."
            )
        }

    override fun build(candidateSignupRequest: CandidateSignupRequest): Candidate {
        val roles: MutableSet<Role> = HashSet()
        val candidateRole: Role = this.getRole()
        roles.add(candidateRole)

        var candidade = Candidate()
        candidade.cep = candidateSignupRequest.cep
        candidade.pcd = candidateSignupRequest.pcd
        candidade.socialNetworkig = candidateSignupRequest.socialNetworking
        candidade.username = candidateSignupRequest.username
        candidade.password = encoder.encode(candidateSignupRequest.password)
        candidade.activated = true
        candidade.roles = roles
        candidade.firstName = candidateSignupRequest.firstName
        candidade.lastName = candidateSignupRequest.lastName
//      TODO: candidade.birthDay = candidateSignupRequest.birthDay
        candidade.cpf = candidateSignupRequest.cpf
        candidade.email = candidateSignupRequest.email
        candidade.phone = candidateSignupRequest.phone

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

        var users = candidateRepository.findAll()

        return users.filterIsInstance<Candidate>()
    }

}
