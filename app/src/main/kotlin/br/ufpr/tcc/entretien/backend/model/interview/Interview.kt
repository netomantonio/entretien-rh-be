package br.ufpr.tcc.entretien.backend.model.interview

import br.ufpr.tcc.entretien.backend.model.Schedule
import br.ufpr.tcc.entretien.backend.model.enums.InterviewStatusTypes
import br.ufpr.tcc.entretien.backend.model.infra.AbstractJpaPersistable
import br.ufpr.tcc.entretien.backend.model.users.Candidate
import br.ufpr.tcc.entretien.backend.model.users.Manager
import br.ufpr.tcc.entretien.backend.model.users.Recruiter
import java.time.Instant
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*


@Entity
class Interview(
    var score: Int? = 0,
    @Column(columnDefinition = "TEXT")
    var recruiterObservation: String? = "",
    @Column(columnDefinition = "TEXT")
    var managerObservation: String? = "",
    @Column(columnDefinition = "TEXT")
    var candidateObservation: String? = "",
    @Column(nullable = false) @Enumerated(EnumType.STRING) var interviewStatus: InterviewStatusTypes = InterviewStatusTypes.WAITING_CANDIDATE,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now(),
    var startingAt: LocalDateTime? = null,
    var endingAt: LocalDateTime? = null,
    var cpf: String? = "",
    @ManyToOne @JoinColumn(name = "fk_recruiter") var recruiter: Recruiter? = null,
    @ManyToOne @JoinColumn(name = "fk_candidate") var candidate: Candidate? = null,
    @ManyToOne @JoinColumn(name = "fk_schedule") var schedule: Schedule? = null,
    var sessionId: String? = null,
    var candidatePresent: Boolean? = false,
    var recruiterPresent: Boolean? = false
) : AbstractJpaPersistable() {

    @ManyToOne
    @JoinColumn(name = "fk_manager")
    lateinit var manager: Manager
}