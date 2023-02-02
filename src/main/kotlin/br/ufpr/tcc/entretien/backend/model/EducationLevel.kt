package br.ufpr.tcc.entretien.backend.model

import br.ufpr.tcc.entretien.backend.model.enums.EEducationLevel
import br.ufpr.tcc.entretien.backend.model.infra.AbstractJpaPersistable
import javax.persistence.*

@Entity
@Table(name = "education_level")
class EducationLevel(
    @Column(nullable = false) @Enumerated(EnumType.STRING) val name: EEducationLevel
): AbstractJpaPersistable()