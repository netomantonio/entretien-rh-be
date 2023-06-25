package br.ufpr.tcc.entretien.backend.datasource.request

import br.ufpr.tcc.entretien.backend.model.enums.InterviewStatusTypes

data class UpdateParcialInterviewRequest(
    val candidateObservation: String? = null,
    val managerObservation: String? = null,
    val score: Long? = null,
    val interviewStatus: InterviewStatusTypes? = null
)
