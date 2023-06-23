package br.ufpr.tcc.entretien.backend.service

import br.ufpr.tcc.entretien.backend.common.utils.sanitizeDocuments
import br.ufpr.tcc.entretien.backend.datasource.request.ManagerSignupRequest
import br.ufpr.tcc.entretien.backend.datasource.response.DashboardRecruiterResponse
import br.ufpr.tcc.entretien.backend.datasource.response.DashboardResponse
import br.ufpr.tcc.entretien.backend.model.enums.ERole
import br.ufpr.tcc.entretien.backend.model.infra.Role
import br.ufpr.tcc.entretien.backend.model.users.Manager
import br.ufpr.tcc.entretien.backend.repository.RoleRepository
import br.ufpr.tcc.entretien.backend.repository.UserRepository
import br.ufpr.tcc.entretien.backend.service.interfaces.IUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class ManagerService : IUserService<Manager, ManagerSignupRequest> {

    @Autowired
    lateinit var managerRepository: UserRepository<Manager>

    @Autowired
    lateinit var interviewService: InterviewService

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var encoder: PasswordEncoder

    override fun existsByUsername(username: String) =
        managerRepository.existsByUsername(username)

    override fun existsByEmail(email: String) =
        managerRepository.existsByEmail(email)

    override fun register(manager: Manager) = managerRepository.save(manager)

    override fun getRole(): Role = roleRepository.findByName(ERole.ROLE_MANAGER)
        .orElseThrow {
            RuntimeException(
                "Error: Role is not found."
            )
        }

    override fun getDashboard(id: Long, from: LocalDate, to: LocalDate): DashboardResponse {
        val lastInterviewCreated = interviewService.getLastByManager(id)
        val thisMonthScheduledInterviews = interviewService.getManagerInterviewsWithinPeriod(id, from, to)
        val interviewsHistory = interviewService.getManagerInterviewHistory(id)
        val interviewsStats = interviewService.getManagerInterviewStats(id)

        var dashboardResponse = DashboardRecruiterResponse()
        if (lastInterviewCreated != null) {
            dashboardResponse.lastUpdate = lastInterviewCreated.createdAt
        } else {
            dashboardResponse.lastUpdate = LocalDateTime.now()
        }
        dashboardResponse.interviewsHistory = interviewsHistory.map { interview -> DashboardResponse.fromInterview(interview) }
        dashboardResponse.thisMonthScheduledInterviews = thisMonthScheduledInterviews.map { interview -> DashboardResponse.fromInterview(interview) }
        dashboardResponse.interviewsStats = interviewsStats

        return dashboardResponse
    }

    override fun build(managerSignupRequest: ManagerSignupRequest): Manager {
        val roles: MutableSet<Role> = HashSet()
        val managerRole: Role = this.getRole()
        roles.add(managerRole)

        var manager = Manager()
        manager.cnpj = managerSignupRequest.cnpj.sanitizeDocuments()
        manager.companyName = managerSignupRequest.companyName
        manager.operationArea = managerSignupRequest.operationArea
        manager.tradingName = managerSignupRequest.tradingName
        manager.username = managerSignupRequest.username
        manager.password = encoder.encode(managerSignupRequest.password)
        manager.activated = true
        manager.roles = roles
        manager.firstName = managerSignupRequest.firstName
        manager.lastName = managerSignupRequest.lastName
//      TODO: candidade.birthDay = candidateSignupRequest.birthDay
        manager.cpf = managerSignupRequest.cpf.sanitizeDocuments()
        manager.email = managerSignupRequest.email
        manager.phone = managerSignupRequest.phone

        return manager
    }

    fun getManagerById(id: Long): Manager = managerRepository.findById(id)
        .orElseThrow {
            RuntimeException(
                "Error: User not found."
            )
        }

}