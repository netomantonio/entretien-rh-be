package br.ufpr.tcc.entretien.backend.model

import br.ufpr.tcc.entretien.backend.model.enums.EEducationLevel
import javax.persistence.*

@Entity
class Resume(
    var presentation: String? = null,
    @ManyToOne
    var educationLevel: EducationLevel? = null,
    @ElementCollection
    var professionalHistory: MutableSet<String>? = null,
    @ElementCollection
    var languages: MutableSet<String>? = null,
    var desiredJobTitle: String? = null
) : AbstractJpaPersistable<Long>()