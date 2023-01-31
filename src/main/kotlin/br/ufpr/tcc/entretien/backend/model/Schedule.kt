package br.ufpr.tcc.entretien.backend.model

import br.ufpr.tcc.entretien.backend.model.infra.AbstractJpaPersistable
import br.ufpr.tcc.entretien.backend.model.users.Candidate
import br.ufpr.tcc.entretien.backend.model.users.Recruiter
import java.time.Instant
import java.util.*
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Temporal
import javax.persistence.TemporalType

/**
 * Entity class that models Schedule-specific attributes.
 */
@Entity
class Schedule(
    @ManyToOne
    @JoinColumn(name = "fk_recruiter")
    var recruiter: Recruiter,
    @ManyToOne
    @JoinColumn(name = "fk_candidate")
    var candidate: Candidate,
    @Temporal(TemporalType.TIMESTAMP) val createdAt: Date = Date.from(Instant.now()),
    @Temporal(TemporalType.TIMESTAMP) val updatedAt: Date = Date.from(Instant.now()),
    @Temporal(TemporalType.TIMESTAMP) val startingAt: Date = Date.from(Instant.now()),
    @Temporal(TemporalType.TIMESTAMP) val endingAt: Date = Date.from(Instant.now()),
    var estimatedDuration: Long? = null
) : AbstractJpaPersistable<Long>()
