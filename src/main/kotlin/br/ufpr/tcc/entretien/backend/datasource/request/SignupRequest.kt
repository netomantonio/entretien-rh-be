package br.ufpr.tcc.entretien.backend.datasource.request

import java.util.Set;

import javax.validation.constraints.*;

data class SignupRequest(
    @NotBlank
    @Size(min = 3, max = 20)
    var username: String,

    @NotBlank
    @Size(max = 50)
    @Email
    var email: String,

    var role: Set<String>,

    @NotBlank
    @Size(min = 6, max = 40)
    var password: String
)