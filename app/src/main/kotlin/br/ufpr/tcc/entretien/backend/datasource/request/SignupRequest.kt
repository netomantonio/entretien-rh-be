package br.ufpr.tcc.entretien.backend.datasource.request

import javax.validation.constraints.*;

open class SignupRequest(
    @NotBlank
    @Size(min = 3, max = 20)
    var username: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var cpf: String = "",
    var phone: String = "",
    @NotBlank
    @Size(max = 50)
    @Email
    var email: String = "",
    var role: Set<String> = emptySet<String>(),
    @NotBlank
    @Size(min = 6, max = 40)
    var password: String = ""
)