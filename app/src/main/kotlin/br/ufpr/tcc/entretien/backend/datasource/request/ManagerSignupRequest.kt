package br.ufpr.tcc.entretien.backend.datasource.request

class ManagerSignupRequest(
    var cnpj: String,
    var companyName: String,
    var tradingName: String,
    var operationArea: String
) : SignupRequest()