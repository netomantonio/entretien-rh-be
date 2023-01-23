package br.ufpr.tcc.entretien.backend.model

import javax.persistence.*

@Entity
class Resume(
    var presentation: String = "",
    @Column(nullable = false) @Enumerated(EnumType.STRING) var educationLevel: EEducationLevel = EEducationLevel.ENSINO_MEDIO,
    @ElementCollection
    var professionalHistory: Set<String> = emptySet(),
    @ElementCollection
    var languages: Set<String> = emptySet(),
    var desiredJobTitle: String = ""
) : AbstractJpaPersistable<Long>()