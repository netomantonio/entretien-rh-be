package br.ufpr.tcc.entretien.backend.model.resume

import br.ufpr.tcc.entretien.backend.model.infra.AbstractJpaPersistable
import com.fasterxml.jackson.annotation.JsonBackReference
import javax.persistence.*

@Entity
class ResumeLanguage(
        var language: String,
        @Column(nullable = false) @Enumerated(EnumType.STRING) var languageProficiencyLevel: LanguageProficiencyLevels,
        @ManyToOne @JoinColumn(name = "fk_resume") @JsonBackReference
        val resume: Resume
) : AbstractJpaPersistable()