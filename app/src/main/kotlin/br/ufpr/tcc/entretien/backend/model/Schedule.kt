package br.ufpr.tcc.entretien.backend.model

import br.ufpr.tcc.entretien.backend.model.enums.DaysOfTheWeek
import br.ufpr.tcc.entretien.backend.model.infra.AbstractJpaPersistable
import br.ufpr.tcc.entretien.backend.model.users.Recruiter
import com.fasterxml.jackson.annotation.JsonBackReference
import java.time.LocalDateTime
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
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    @Enumerated(EnumType.ORDINAL) var dayOfTheWeek: DaysOfTheWeek,
    @Basic var startingAt: LocalTime,
    @Basic var endingAt: LocalTime
) : AbstractJpaPersistable()
