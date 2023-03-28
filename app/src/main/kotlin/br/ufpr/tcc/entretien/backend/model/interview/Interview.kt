package br.ufpr.tcc.entretien.backend.model.interview

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
    var mainObservation: String? = "",
    var managerObservation: String? = "",
    var candidateObservation: String? = "",
    @Column(nullable = false) @Enumerated(EnumType.STRING) var interviewStatus: InterviewStatusTypes = InterviewStatusTypes.WAITING_CANDIDATE,
    @Temporal(TemporalType.TIMESTAMP) val createdAt: Date = Date.from(Instant.now()),
    @Temporal(TemporalType.TIMESTAMP) val updatedAt: Date = Date.from(Instant.now()),
    var startingAt: LocalDateTime? = null,
    var endingAt: LocalDateTime? = null,
    var cpf: String? = "",
    @ManyToOne
    @JoinColumn(name = "fk_recruiter")
    var recruiter: Recruiter? = null
) : AbstractJpaPersistable() {
    @ManyToOne
    @JoinColumn(name = "fk_candidate")
    lateinit var candidate: Candidate

    @ManyToOne
    @JoinColumn(name = "fk_manager")
    lateinit var manager: Manager
}