package br.ufpr.tcc.entretien.backend.service

import br.ufpr.tcc.entretien.backend.datasource.request.SignupRequest
import br.ufpr.tcc.entretien.backend.model.enums.ERole
import br.ufpr.tcc.entretien.backend.model.infra.Role
import br.ufpr.tcc.entretien.backend.model.users.Admin
import br.ufpr.tcc.entretien.backend.model.users.User
import br.ufpr.tcc.entretien.backend.repository.RoleRepository
import br.ufpr.tcc.entretien.backend.repository.UserRepository
import br.ufpr.tcc.entretien.backend.service.interfaces.IUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.function.Consumer

@Service
class UserService : IUserService<Admin, SignupRequest> {

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var userRepository: UserRepository<Admin>

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
        admin.cpf = signupRequest.cpf
        admin.email = signupRequest.email
        admin.phone = signupRequest.phone

        return admin
    }

    override fun register(admin: Admin): Admin = userRepository.save(admin)

    fun getAll(): Iterable<User> {
        return userRepository.findAll()
    }
}