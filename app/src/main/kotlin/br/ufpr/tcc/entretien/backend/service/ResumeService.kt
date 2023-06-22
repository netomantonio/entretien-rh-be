package br.ufpr.tcc.entretien.backend.service

import br.ufpr.tcc.entretien.backend.datasource.request.CandidateResumeRequest
import br.ufpr.tcc.entretien.backend.model.resume.*
import br.ufpr.tcc.entretien.backend.model.users.Candidate
import br.ufpr.tcc.entretien.backend.repository.ResumeRepository
import br.ufpr.tcc.entretien.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ResumeService {

    @Autowired
    lateinit var resumeRepository: ResumeRepository

    @Autowired
    lateinit var candidateRepository: UserRepository<Candidate>

    fun getResumeByCandidateId(id: Long): Resume {
        return resumeRepository.findByCandidateId(id).get()
    }

    fun buildNewResume(candidate: Candidate): Resume {
        var resume = resumeRepository.save(
            Resume(
                "",
                "",
                EducationLevelTypes.ENSINO_FUNDAMENTAL,
                mutableListOf(),
                mutableListOf(),
                mutableListOf(),
                candidate
            )
        )
        return resumeRepository.save(resume)
    }

    fun buildResume(candidateResumeRequest: CandidateResumeRequest, candidate: Candidate): Resume {
        var resume = resumeRepository.save(
            Resume(
                candidateResumeRequest.desiredJobTitle,
                candidateResumeRequest.presentation,
                candidateResumeRequest.educationLevel,
                mutableListOf(),
                mutableListOf(),
                mutableListOf(),
                candidate
            )
        )

        if (candidateResumeRequest.academicEducation != null) {
            for (academicEntry in candidateResumeRequest.academicEducation!!) {
                var resumeAcademicEducation = ResumeAcademicEducation(
                    academicEntry.institution,
                    academicEntry.courseName,
                    academicEntry.ongoing,
                    academicEntry.startedAt,
                    academicEntry.endedAt,
                    resume
                )
                resume.academicEducation?.add(resumeAcademicEducation)
            }
        }

        if (candidateResumeRequest.professionalExperience != null) {
            for (professionalEntry in candidateResumeRequest.professionalExperience!!) {
                var resumeProfessionalExperience = ResumeProfessionalExperience(
                    professionalEntry.position,
                    professionalEntry.company,
                    professionalEntry.jobDescription,
                    professionalEntry.currentPosition,
                    professionalEntry.startedAt,
                    professionalEntry.endedAt,
                    resume
                )
                resume.professionalExperience.add(resumeProfessionalExperience)
            }
        }

        if (candidateResumeRequest.languages != null) {
            for (language in candidateResumeRequest.languages!!) {
                var resumeLanguage = ResumeLanguage(
                    language.language,
                    language.languageProficiencyLevel,
                    resume
                )
                resume.languages?.add(resumeLanguage)
            }
        }

        return resumeRepository.save(resume)
    }

    fun updateResume(candidateResumeRequest: CandidateResumeRequest, candidate: Candidate): Resume {

        candidate.resume.desiredJobTitle = candidateResumeRequest.desiredJobTitle
        candidate.resume.presentation = candidateResumeRequest.presentation
        candidate.resume.educationLevel = candidateResumeRequest.educationLevel

        if (candidateResumeRequest.academicEducation != null) {
            for (academicEntry in candidateResumeRequest.academicEducation!!) {
                var resumeAcademicEducation = ResumeAcademicEducation(
                    academicEntry.institution,
                    academicEntry.courseName,
                    academicEntry.ongoing,
                    academicEntry.startedAt,
                    academicEntry.endedAt,
                    candidate.resume
                )
                candidate.resume.academicEducation?.add(resumeAcademicEducation)
            }
        }

        if (candidateResumeRequest.professionalExperience != null) {
            for (professionalEntry in candidateResumeRequest.professionalExperience!!) {
                var resumeProfessionalExperience = ResumeProfessionalExperience(
                    professionalEntry.position,
                    professionalEntry.company,
                    professionalEntry.jobDescription,
                    professionalEntry.currentPosition,
                    professionalEntry.startedAt,
                    professionalEntry.endedAt,
                    candidate.resume
                )
                candidate.resume.professionalExperience.add(resumeProfessionalExperience)
            }
        }

        if (candidateResumeRequest.languages != null) {
            for (language in candidateResumeRequest.languages!!) {
                var resumeLanguage = ResumeLanguage(
                    language.language,
                    language.languageProficiencyLevel,
                    candidate.resume
                )
                candidate.resume.languages?.add(resumeLanguage)
            }
        }

        return candidateRepository.save(candidate).resume
    }

    fun getCandidateResumeLastUpdate(candidateId: Long): LocalDateTime {
        return resumeRepository.getCandidateResumeLastUpdate(candidateId)
    }

}