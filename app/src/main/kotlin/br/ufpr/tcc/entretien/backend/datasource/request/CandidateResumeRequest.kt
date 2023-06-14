package br.ufpr.tcc.entretien.backend.datasource.request

import br.ufpr.tcc.entretien.backend.model.resume.LanguageProficiencyLevels
import br.ufpr.tcc.entretien.backend.model.resume.EducationLevelTypes
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDate
import javax.persistence.Column
import javax.persistence.EnumType
import javax.persistence.Enumerated

class CandidateResumeRequest(
    var desiredJobTitle: String?,
    var presentation: String?,
    var educationLevel: EducationLevelTypes,
    var academicEducation: List<AcademicEducation>?,
    var professionalExperience: List<ProfessionalExperience>?,
    var languages: List<Language>?
) {
    class AcademicEducation(
        val institution: String,
        val courseName: String,
        val ongoing: Boolean,
        @JsonFormat(pattern = "yyyy-MM-dd")
        val startedAt: LocalDate,
        @JsonFormat(pattern = "yyyy-MM-dd") @JsonInclude(JsonInclude.Include.NON_NULL)
        val endedAt: LocalDate? = null
    )

    class ProfessionalExperience(
        val position: String,
        val company: String,
        val jobDescription: String,
        val currentPosition: Boolean,
        @JsonFormat(pattern = "yyyy-MM-dd") val startedAt: LocalDate,
        @JsonFormat(pattern = "yyyy-MM-dd") @JsonInclude(JsonInclude.Include.NON_NULL) val endedAt: LocalDate?
    )

    class Language(
        var language: String,
        @Column(nullable = false) @Enumerated(EnumType.STRING) var languageProficiencyLevel: LanguageProficiencyLevels
    )
}