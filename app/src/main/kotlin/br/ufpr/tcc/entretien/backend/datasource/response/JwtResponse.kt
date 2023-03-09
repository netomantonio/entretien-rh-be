package br.ufpr.tcc.entretien.backend.datasource.response

data class JwtResponse(
    var token: String,
    var type: String = "Bearer",
    var id: Long,
    var username: String,
    var email: String,
    var roles: List<String>
) {
    constructor(token: String, id: Long, username: String, email: String, roles: List<String>) : this(
        token,
        "Bearer",
        id,
        username,
        email,
        roles
    )
}