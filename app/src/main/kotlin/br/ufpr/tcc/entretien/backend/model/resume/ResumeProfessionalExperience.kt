package br.ufpr.tcc.entretien.backend.model.resume

import br.ufpr.tcc.entretien.backend.model.infra.AbstractJpaPersistable
import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonInclude.Include
import java.time.LocalDate
import java.util.*
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

@Entity
class ResumeProfessionalExperience(
    val position: String,
    val company: String,
    val jobDescription: String,
    val currentPosition: Boolean,
    @JsonFormat(pattern = "yyyy-MM-dd") val startedAt: LocalDate? = null,
    @JsonFormat(pattern = "yyyy-MM-dd") @JsonInclude(Include.NON_NULL) val endedAt: LocalDate? = null,
    @ManyToOne @JoinColumn(name = "fk_resume") @JsonBackReference
    val resume: Resume
) : AbstractJpaPersistable()