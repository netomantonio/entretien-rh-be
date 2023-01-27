package br.ufpr.tcc.entretien.backend.service

import br.ufpr.tcc.entretien.backend.model.*
import br.ufpr.tcc.entretien.backend.repository.EducationLevelRepository
import br.ufpr.tcc.entretien.backend.repository.RoleRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.function.Consumer

@Service
class UserService {

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var educationLevelRepository: EducationLevelRepository

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

    fun getEducationLevel(educationLevel: String): EducationLevel {
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

    fun buildResume(
        presentation: String,
        educationLevel: String,
        professionalHistory: Set<String>,
        languages: Set<String>,
        desiredJobTitle: String
    ): Resume {

        val educationLevel = this.getEducationLevel(educationLevel)
        return Resume(presentation, educationLevel, professionalHistory, languages, desiredJobTitle)
    }

}