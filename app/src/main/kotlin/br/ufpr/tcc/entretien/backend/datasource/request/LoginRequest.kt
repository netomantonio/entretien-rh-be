package br.ufpr.tcc.entretien.backend.datasource.request
import javax.validation.constraints.NotBlank;

class LoginRequest (
    @NotBlank var username: String,
    @NotBlank var password: String
)
