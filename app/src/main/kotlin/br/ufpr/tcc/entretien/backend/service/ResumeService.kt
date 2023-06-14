package br.ufpr.tcc.entretien.backend.service

import br.ufpr.tcc.entretien.backend.datasource.request.CandidateResumeRequest
import br.ufpr.tcc.entretien.backend.model.resume.*
import br.ufpr.tcc.entretien.backend.model.users.Candidate
import br.ufpr.tcc.entretien.backend.repository.ResumeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class ResumeService {

    @Autowired
    lateinit var resumeRepository: ResumeRepository

    fun getResumeByCandidateId(id: Long): Resume {
        return resumeRepository.findByCandidateId(id).get()
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

        if(candidateResumeRequest.academicEducation != null){
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

        if(candidateResumeRequest.professionalExperience != null){
            for (professionalEntry in candidateResumeRequest.professionalExperience!!){
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

        if(candidateResumeRequest.languages != null){
            for(language in candidateResumeRequest.languages!!){
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

}