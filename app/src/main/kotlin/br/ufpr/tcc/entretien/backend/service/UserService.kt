package br.ufpr.tcc.entretien.backend.service

import br.ufpr.tcc.entretien.backend.common.utils.sanitizeDocuments
import br.ufpr.tcc.entretien.backend.datasource.request.SignupRequest
import br.ufpr.tcc.entretien.backend.datasource.response.DashboardAdminResponse
import br.ufpr.tcc.entretien.backend.model.enums.ERole
import br.ufpr.tcc.entretien.backend.model.enums.InterviewStatusTypes
import br.ufpr.tcc.entretien.backend.model.infra.Role
import br.ufpr.tcc.entretien.backend.model.interview.Interview
import br.ufpr.tcc.entretien.backend.model.users.Admin
import br.ufpr.tcc.entretien.backend.model.users.User
import br.ufpr.tcc.entretien.backend.repository.RoleRepository
import br.ufpr.tcc.entretien.backend.repository.ScheduleRepository
import br.ufpr.tcc.entretien.backend.repository.UserRepository
import br.ufpr.tcc.entretien.backend.service.interfaces.IUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.function.Consumer

@Service
class UserService : IUserService<Admin, SignupRequest> {

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var userRepository: UserRepository<Admin>

    @Autowired
    lateinit var schedulesRepository: ScheduleRepository

    @Autowired
    lateinit var interviewService: InterviewService

    @Autowired
    lateinit var encoder: PasswordEncoder

    fun getRoles(roles: Set<String>): MutableSet<Role> {

        val strRoles: Set<String> = setOf(roles.toString())
        val roles: MutableSet<Role> = HashSet()
        if (strRoles == null) {
            throw (RuntimeException("Error: Role is not found."))
        } else {
            strRoles.forEach(Consumer { role: String? ->
                when (role) {
                    "Admin" -> {
                        val adminRole: Role = roleRepository.findByName(ERole.ROLE_ADMIN)
                            .orElseThrow {
                                RuntimeException(
                                    "Error: Role is not found."
                                )
                            }
                        roles.add(adminRole)
                    }

                    "Manager" -> {
                        val modRole: Role = roleRepository.findByName(ERole.ROLE_MANAGER)
                            .orElseThrow {
                                RuntimeException(
                                    "Error: Role is not found."
                                )
                            }
                        roles.add(modRole)
                    }

                    "Recruiter" -> {
                        val modRole: Role = roleRepository.findByName(ERole.ROLE_RECRUITER)
                            .orElseThrow {
                                RuntimeException(
                                    "Error: Role is not found."
                                )
                            }
                        roles.add(modRole)
                    }

                    else -> {
                        val userRole: Role = roleRepository.findByName(ERole.ROLE_CANDIDATE)
                            .orElseThrow {
                                RuntimeException(
                                    "Error: Role is not found."
                                )
                            }
                        roles.add(userRole)
                    }
                }
            })
        }

        return roles
    }

    override fun existsByUsername(username: String): Boolean = userRepository.existsByUsername(username)

    override fun existsByEmail(email: String): Boolean = userRepository.existsByEmail(email)

    override fun getRole(): Role = roleRepository.findByName(ERole.ROLE_ADMIN)
        .orElseThrow {
            RuntimeException(
                "Error: Role is not found."
            )
        }

    override fun getDashboard(id: Long, from: LocalDate, to: LocalDate): DashboardAdminResponse {
        val thisMonthScheduledInterviews = interviewService.getAllInterviewsByStatusWithinPeriod(InterviewStatusTypes.SCHEDULE, from, to)
        val interviewProblemHistory = interviewService.getInterviewProblemHistory()
        val interviewsStats = interviewService.getInterviewStats()

        val candidatesQtd = userRepository.getUserQtdByRole(ERole.ROLE_CANDIDATE.name)
        val managersQtd = userRepository.getUserQtdByRole(ERole.ROLE_MANAGER.name)
        val recruitersQtd = userRepository.getUserQtdByRole(ERole.ROLE_RECRUITER.name)
        val schedulesQtd = schedulesRepository.getAllQtd()
        val unregisteredCpfQtd = interviewService.getUnregisteredCpfQtd()
        val interviewsAbsentCandidateQtd = getCountOf(interviewProblemHistory, InterviewStatusTypes.ABSENT_CANDIDATE)
        val interviewsAbsentRecruiterQtd = getCountOf(interviewProblemHistory, InterviewStatusTypes.ABSENT_RECRUITER)
        val interviewsDidNotOccur = getCountOf(interviewProblemHistory, InterviewStatusTypes.DID_NOT_OCCUR)

        var adminStats: DashboardAdminResponse.AdminStats = DashboardAdminResponse.AdminStats()
        adminStats.scheduled = interviewsStats.scheduled
        adminStats.toBeScheduled = interviewsStats.toBeScheduled
        adminStats.completed = interviewsStats.completed
        adminStats.total = interviewsStats.total

        var dashboardAdminResponse = DashboardAdminResponse()
        dashboardAdminResponse.lastUpdate = LocalDateTime.now()
        dashboardAdminResponse.interviewProblemHistory = interviewProblemHistory
        dashboardAdminResponse.thisMonthScheduledInterviews = thisMonthScheduledInterviews
        dashboardAdminResponse.interviewsStats = adminStats
        dashboardAdminResponse.interviewsStats.candidatesQtd = candidatesQtd
        dashboardAdminResponse.interviewsStats.managersQtd = managersQtd
        dashboardAdminResponse.interviewsStats.recrutiersQtd = recruitersQtd
        dashboardAdminResponse.interviewsStats.schedulesQtd = schedulesQtd
        dashboardAdminResponse.interviewsStats.unregistredCpfQtd = unregisteredCpfQtd
        dashboardAdminResponse.interviewsStats.interviewsAbsentCandidateQtd = interviewsAbsentCandidateQtd.toLong()
        dashboardAdminResponse.interviewsStats.interviewsAbsentRecruiterQtd = interviewsAbsentRecruiterQtd.toLong()
        dashboardAdminResponse.interviewsStats.interviewsDidNotOccur = interviewsDidNotOccur.toLong()

        return dashboardAdminResponse
    }

    fun getCountOf(list: List<Interview>, status: InterviewStatusTypes): Int {
        val grouping = list.groupingBy { it.interviewStatus }.eachCount()
        return grouping[status] ?: 0
    }

    override fun build(signupRequest: SignupRequest): Admin {
        val roles: MutableSet<Role> = HashSet()
        val adminRole: Role = this.getRole()
        roles.add(adminRole)

        var admin = Admin()


        admin.username = signupRequest.username
        admin.password = encoder.encode(signupRequest.password)
        admin.activated = true
        admin.roles = roles
        admin.firstName = signupRequest.firstName
        admin.lastName = signupRequest.lastName
//      admindidade.birthDay = signupRequest.birthDay
        admin.cpf = signupRequest.cpf.sanitizeDocuments()
        admin.email = signupRequest.email
        admin.phone = signupRequest.phone

        return admin
    }

    override fun register(admin: Admin): Admin = userRepository.save(admin)

    fun getAll(): Iterable<User> {
        return userRepository.findAll()
    }
}