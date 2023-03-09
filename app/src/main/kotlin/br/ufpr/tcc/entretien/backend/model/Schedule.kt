package br.ufpr.tcc.entretien.backend.model

import br.ufpr.tcc.entretien.backend.model.enums.EDayOfTheWeek
import br.ufpr.tcc.entretien.backend.model.infra.AbstractJpaPersistable
import br.ufpr.tcc.entretien.backend.model.users.Recruiter
import com.fasterxml.jackson.annotation.JsonBackReference
import java.time.Instant
import java.time.LocalTime
import java.util.*
import javax.persistence.*

/**
 * Entity class that models Schedule-specific attributes.
 */
@Entity
class Schedule(
    @ManyToOne
    @JoinColumn(name = "fk_recruiter")
    @JsonBackReference
    val recruiter: Recruiter,
    var available: Boolean = true,
    @Temporal(TemporalType.TIMESTAMP) val createdAt: Date = Date.from(Instant.now()),
    @Temporal(TemporalType.TIMESTAMP) var updatedAt: Date = Date.from(Instant.now()),
    @Enumerated(EnumType.ORDINAL) var dayOfTheWeek: EDayOfTheWeek,
    @Basic var startingAt: LocalTime,
    @Basic var endingAt: LocalTime
) : AbstractJpaPersistable()
