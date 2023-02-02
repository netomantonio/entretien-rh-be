package br.ufpr.tcc.entretien.backend.service

import br.ufpr.tcc.entretien.backend.datasource.request.RecruiterScheduleRequest
import br.ufpr.tcc.entretien.backend.datasource.request.RecruiterSignupRequest
import br.ufpr.tcc.entretien.backend.model.Schedule
import br.ufpr.tcc.entretien.backend.model.enums.EDayOfTheWeek
import br.ufpr.tcc.entretien.backend.model.enums.ERole
import br.ufpr.tcc.entretien.backend.model.infra.Role
import br.ufpr.tcc.entretien.backend.model.users.Recruiter
import br.ufpr.tcc.entretien.backend.repository.RoleRepository
import br.ufpr.tcc.entretien.backend.repository.UserRepository
import br.ufpr.tcc.entretien.backend.service.interfaces.IUserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalTime

@Service
class RecruiterService : IUserService<Recruiter, RecruiterSignupRequest> {

    @Autowired
    lateinit var recruiterRepository: UserRepository<Recruiter>

    @Autowired
    lateinit var roleRepository: RoleRepository

    @Autowired
    lateinit var encoder: PasswordEncoder

    override fun existsByUsername(username: String) =
        recruiterRepository.existsByUsername(username)

    override fun existsByEmail(email: String) =
        recruiterRepository.existsByEmail(email)

    override fun register(recruiter: Recruiter) = recruiterRepository.save(recruiter)

    override fun getRole(): Role = roleRepository.findByName(ERole.ROLE_RECRUITER)
        .orElseThrow {
            RuntimeException(
                "Error: Role is not found."
            )
        }

    override fun build(candidateSignupRequest: RecruiterSignupRequest): Recruiter {
        val roles: MutableSet<Role> = HashSet()
        val recruiterRole: Role = this.getRole()
        roles.add(recruiterRole)

        var recruiter = Recruiter()
        recruiter.cnpj = candidateSignupRequest.cnpj
        recruiter.presentation = candidateSignupRequest.presentation
        recruiter.professionalRecord = candidateSignupRequest.professionalRecord
        recruiter.specialities = candidateSignupRequest.specialities
        recruiter.username = candidateSignupRequest.username
        recruiter.password = encoder.encode(candidateSignupRequest.password)
        recruiter.activated = true
        recruiter.roles = roles
        recruiter.firstName = candidateSignupRequest.firstName
        recruiter.lastName = candidateSignupRequest.lastName
//      TODO: candidade.birthDay = candidateSignupRequest.birthDay
        recruiter.cpf = candidateSignupRequest.cpf
        recruiter.email = candidateSignupRequest.email
        recruiter.phone = candidateSignupRequest.phone

        return recruiter
    }

    // TODO: Schedule builder(?); Setter/Getter;

    fun addScheduleEntry(recruiterScheduleRequest: RecruiterScheduleRequest){
        // TODO: check recruiterId
        var recruiter = this.getRecruiterById(recruiterScheduleRequest.recruiterId)

        if(recruiter.schedule == null){
            recruiter.schedule = mutableSetOf()
        }

        val schedule = this.buildSchedule(recruiter, recruiterScheduleRequest.dayOfTheWeek, recruiterScheduleRequest.startingAt, recruiterScheduleRequest.endingAt)

        recruiter.schedule!!.add(schedule)

        this.recruiterRepository.save(recruiter)
    }

    // TODO:
    fun buildSchedule(
        recruiter: Recruiter,
        dayOfTheWeek: EDayOfTheWeek,
        startingAt: LocalTime,
        endingAt: LocalTime
    ): Schedule {
        return Schedule(recruiter = recruiter, dayOfTheWeek = dayOfTheWeek, startingAt = startingAt, endingAt = endingAt)
    }

    fun getRecruiterById(id: Long): Recruiter = recruiterRepository.findById(id)
        .orElseThrow {
            RuntimeException(
                "Error: User not found."
            )
        }

}