package br.ufpr.tcc.entretien.backend.controller

import br.ufpr.tcc.entretien.backend.datasource.request.CandidateSignupRequest
import br.ufpr.tcc.entretien.backend.model.Candidate
import br.ufpr.tcc.entretien.backend.model.ERole
import br.ufpr.tcc.entretien.backend.model.Role
import br.ufpr.tcc.entretien.backend.repository.RoleRepository
import br.ufpr.tcc.entretien.backend.repository.UserRepository
import br.ufpr.tcc.entretien.backend.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/candidate")
class CandidateController {

    @Autowired
    lateinit var userService: UserService

    @Autowired
    lateinit var candidateRepository: UserRepository<Candidate>
    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var encoder: PasswordEncoder

    @PostMapping("")
    fun registerCandidate(@Valid @RequestBody candidateSignupRequest: CandidateSignupRequest): ResponseEntity<*> {
        if (candidateRepository.existsByUsername(candidateSignupRequest.username)) {
            return ResponseEntity
                .badRequest()
                .body<Any>(("Error: Username is already taken!"))
        }
        if (candidateSignupRequest != null) {
            if (candidateRepository.existsByEmail(candidateSignupRequest.email)) {
                return ResponseEntity
                    .badRequest()
                    .body<Any>("Error: Email is already in use!")
            }
        }

        val roles: MutableSet<Role> = HashSet()
        val userRole: Role = roleRepository.findByName(ERole.ROLE_CANDIDATE)
            .orElseThrow {
                RuntimeException(
                    "Error: Role is not found."
                )
            }
        roles.add(userRole)

        var candidade = Candidate()
        candidade.cep = candidateSignupRequest.cep
        candidade.pcd = candidateSignupRequest.pcd
        candidade.socialNetworkig = candidateSignupRequest.socialNetworkig
//      TODO: candidade.resume = candidateSignupRequest.resume
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

        candidade.resume = userService.buildResume(
            candidateSignupRequest.presentation,
            candidateSignupRequest.educationLevel,
            candidateSignupRequest.professionalHistory,
            candidateSignupRequest.languages,
            candidateSignupRequest.desiredJobTitle
        )

        try {
            candidateRepository.save(candidade)
            return ResponseEntity.ok<Any>("User registered successfully!")
        } catch (ex: Exception) {
            println("[ERROR] ------------------------------------------")
            println("[MESSAGE]")
            println(ex.message)
            println("[STACKTRACE]")
            println(ex.stackTrace)
            return ResponseEntity.internalServerError().body("Persistence error.")
        }

    }

}