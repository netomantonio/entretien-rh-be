package br.ufpr.tcc.entretien.backend.datasource.request

class CandidateResumeRequest(
    var candidateId: Long,
    var resumeId: Long?,
    var presentation: String,
    var educationLevel: String,
    var professionalHistory: MutableSet<String>,
    var languages: MutableSet<String>,
    var desiredJobTitle: String
)