package br.ufpr.tcc.entretien.backend.datasource.request

class UpdateCandidateDataRequest(
    var socialNetworking: String,
    var pcd: Boolean,
    var cep: String
) : UpdateUserDataRequest()