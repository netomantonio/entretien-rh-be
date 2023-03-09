package br.ufpr.tcc.entretien.backend.datasource.request

class RecruiterSignupRequest(
    var professionalRecord: String,
    var presentation: String,
    var cnpj: String,
    var specialities: String
) : SignupRequest()