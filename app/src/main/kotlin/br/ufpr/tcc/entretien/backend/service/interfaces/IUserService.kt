package br.ufpr.tcc.entretien.backend.service.interfaces

import br.ufpr.tcc.entretien.backend.datasource.request.SignupRequest
import br.ufpr.tcc.entretien.backend.model.infra.Role
import br.ufpr.tcc.entretien.backend.model.users.User

interface IUserService<T : User, SR : SignupRequest> {
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
    fun register(user: T): T
    fun build(signupRequest: SR): T
    fun getRole(): Role
}