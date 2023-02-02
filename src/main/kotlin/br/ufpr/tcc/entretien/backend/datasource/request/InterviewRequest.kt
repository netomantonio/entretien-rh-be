package br.ufpr.tcc.entretien.backend.datasource.request

class InterviewRequest (
    val candidateId: Long,
    val managerId: Long,
    val managerObservation: String = ""
)
