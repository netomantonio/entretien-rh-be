package br.ufpr.tcc.entretien.backend.model

import br.ufpr.tcc.entretien.backend.model.enums.EDayOfTheWeek
import br.ufpr.tcc.entretien.backend.model.infra.AbstractJpaPersistable
import br.ufpr.tcc.entretien.backend.model.users.Candidate
import br.ufpr.tcc.entretien.backend.model.users.Recruiter
import java.time.Instant
import java.util.*
import javax.persistence.*

/**
 * Entity class that models Schedule-specific attributes.
 */
@Entity
class Schedule(
    @ManyToOne
    @JoinColumn(name = "fk_recruiter")
    var recruiter: Recruiter,
    @Temporal(TemporalType.TIMESTAMP) val createdAt: Date = Date.from(Instant.now()),
    @Temporal(TemporalType.TIMESTAMP) val updatedAt: Date = Date.from(Instant.now()),
    @Enumerated(EnumType.ORDINAL)
    var dayOfTheWeek: EDayOfTheWeek
) : AbstractJpaPersistable<Long>()
