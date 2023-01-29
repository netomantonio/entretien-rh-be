package br.ufpr.tcc.entretien.backend.datasource.request

class CandidateSignupRequest(
    var socialNetworkig: String,
    var pcd: Boolean,
    var cep: String
) : SignupRequest()