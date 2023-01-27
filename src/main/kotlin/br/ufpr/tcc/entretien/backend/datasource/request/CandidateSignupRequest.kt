package br.ufpr.tcc.entretien.backend.datasource.request

import br.ufpr.tcc.entretien.backend.model.Resume

class CandidateSignupRequest(
    var socialNetworkig: String,
    var pcd: Boolean,
    var cep: String,
    var presentation: String,
    var educationLevel: String,
    var professionalHistory: Set<String>,
    var languages: Set<String>,
    var desiredJobTitle: String
) : SignupRequest()