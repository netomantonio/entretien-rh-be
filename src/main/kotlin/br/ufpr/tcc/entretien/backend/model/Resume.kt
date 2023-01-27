package br.ufpr.tcc.entretien.backend.model

import javax.persistence.*

@Entity
class Resume(
    var presentation: String = "",
    @ManyToOne
    var educationLevel: EducationLevel = EducationLevel(EEducationLevel.ENSINO_FUNDAMENTAL),
    @ElementCollection
    var professionalHistory: Set<String> = emptySet(),
    @ElementCollection
    var languages: Set<String> = emptySet(),
    var desiredJobTitle: String = ""
) : AbstractJpaPersistable<Long>()