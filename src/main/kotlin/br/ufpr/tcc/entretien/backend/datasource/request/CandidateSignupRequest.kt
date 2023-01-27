package br.ufpr.tcc.entretien.backend.datasource.request

import br.ufpr.tcc.entretien.backend.model.Resume

class CandidateSignupRequest(
    var socialNetworkig: String,
    var pcd: Boolean,
    var cep: String
) : SignupRequest()