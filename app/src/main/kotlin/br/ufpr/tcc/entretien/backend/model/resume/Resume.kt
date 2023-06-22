package br.ufpr.tcc.entretien.backend.model.resume

import br.ufpr.tcc.entretien.backend.model.infra.AbstractJpaPersistable
import br.ufpr.tcc.entretien.backend.model.users.Candidate
import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonManagedReference
import java.time.Instant
import java.util.*
import javax.persistence.*

@Entity
class Resume(
    var desiredJobTitle: String? = null,
    var presentation: String? = null,
    @Column(nullable = false) @Enumerated(EnumType.STRING)
    var educationLevel: EducationLevelTypes? = null,
    @OneToMany(cascade=[CascadeType.ALL], mappedBy = "resume") @JsonManagedReference
    var academicEducation: MutableList<ResumeAcademicEducation>?,
    @OneToMany(cascade=[CascadeType.ALL], mappedBy = "resume") @JsonManagedReference
    var professionalExperience: MutableList<ResumeProfessionalExperience>,
    @OneToMany(cascade=[CascadeType.ALL], mappedBy = "resume") @JsonManagedReference
    var languages: MutableList<ResumeLanguage>? = null,
    @OneToOne(cascade=[CascadeType.ALL])
    @JoinColumn(name = "fk_candidate", referencedColumnName = "id")
    @JsonBackReference
    var candidate: Candidate,
    @Temporal(TemporalType.TIMESTAMP) open val createdAt: Date = Date.from(Instant.now()),
    @Temporal(TemporalType.TIMESTAMP) open var updatedAt: Date = Date.from(Instant.now())
) : AbstractJpaPersistable()