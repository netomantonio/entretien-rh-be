package br.ufpr.tcc.entretien.backend.datasource.request

import javax.validation.constraints.NotBlank

data class AdminUpdateRequest(
    val firstName: String,
    val lastName: String,
    val phone: String,
    val email: String,
    @field:NotBlank
    val birthDay: String
)