package br.ufpr.tcc.entretien.backend.model

import br.ufpr.tcc.entretien.backend.model.enums.EducationLevelTypes
import br.ufpr.tcc.entretien.backend.model.infra.AbstractJpaPersistable
import br.ufpr.tcc.entretien.backend.model.users.Candidate
import javax.persistence.*

@Entity
class Resume(
    var presentation: String? = null,
    @Column(nullable = false) @Enumerated(EnumType.STRING)
    var educationLevel: EducationLevelTypes? = null,
    @ElementCollection
    var professionalHistory: MutableSet<String>? = null,
    @ElementCollection
    var languages: MutableSet<String>? = null,
    var desiredJobTitle: String? = null,
    @OneToOne(mappedBy = "resume")
    var candidate: Candidate
) : AbstractJpaPersistable()