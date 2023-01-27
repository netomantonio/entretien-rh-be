package br.ufpr.tcc.entretien.backend.service

import br.ufpr.tcc.entretien.backend.model.*
import br.ufpr.tcc.entretien.backend.model.enums.EEducationLevel
import br.ufpr.tcc.entretien.backend.model.enums.ERole
import br.ufpr.tcc.entretien.backend.repository.EducationLevelRepository
import br.ufpr.tcc.entretien.backend.repository.RoleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.function.Consumer

@Service
class UserService {

    @Autowired
    lateinit var roleRepository: RoleRepository

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

}