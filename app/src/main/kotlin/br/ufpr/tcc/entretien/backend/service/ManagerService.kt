package br.ufpr.tcc.entretien.backend.service

import br.ufpr.tcc.entretien.backend.datasource.request.ManagerSignupRequest
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

@Service
class ManagerService : IUserService<Manager, ManagerSignupRequest> {

    @Autowired
    lateinit var managerRepository: UserRepository<Manager>

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
        TODO("Not yet implemented")
    }

    override fun build(managerSignupRequest: ManagerSignupRequest): Manager {
        val roles: MutableSet<Role> = HashSet()
        val managerRole: Role = this.getRole()
        roles.add(managerRole)

        var manager = Manager()
        manager.cnpj = managerSignupRequest.cnpj
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
        manager.cpf = managerSignupRequest.cpf
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