package br.ufpr.tcc.entretien.backend.datasource.response

import br.ufpr.tcc.entretien.backend.model.enums.InterviewStatusTypes
import br.ufpr.tcc.entretien.backend.model.interview.Interview
import java.time.LocalDateTime

data class CandidateNextInterviewResponse(
    val id: Long,
    val companyName: String,
    val startingAt: LocalDateTime?,
    val status: InterviewStatusTypes,
){
    companion object {
        fun fromInterview(interview: Interview): CandidateNextInterviewResponse{
            return CandidateNextInterviewResponse(interview.getId(), interview.manager.companyName, interview.startingAt, interview.interviewStatus)
        }
    }
}
