package br.ufpr.tcc.entretien.backend.datasource.response

import com.fasterxml.jackson.annotation.JsonInclude

data class InterviewsByCandidateResponse(
    val interviews: List<InterviewByCandidateResponse>? = listOf()
)
@JsonInclude(JsonInclude.Include.NON_NULL)
data class InterviewByCandidateResponse(
    val id: String,
    val companyName: String,
    val status: String,
    val appointmentDate: String? = null,
    val sessionId: String? = null
)
