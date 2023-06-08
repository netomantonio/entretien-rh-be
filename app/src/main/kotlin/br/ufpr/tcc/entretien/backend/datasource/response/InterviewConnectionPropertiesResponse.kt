package br.ufpr.tcc.entretien.backend.datasource.response

data class InterviewConnectionPropertiesResponse(
    val candidateInterviewVideoCallTokenAccess: String,
    val candidateInterviewVideoCallRoomId: String,
    val candidateInterviewVideoCallRoomName: String
)