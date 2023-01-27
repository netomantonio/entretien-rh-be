package br.ufpr.tcc.entretien.backend.service.interfaces

import br.ufpr.tcc.entretien.backend.datasource.request.SignupRequest
import br.ufpr.tcc.entretien.backend.model.Role
import br.ufpr.tcc.entretien.backend.model.User

interface IUserService<T : User, SR : SignupRequest> {
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
    fun register(user: T): User
    fun build(signupRequest: SR): T
    fun getRole(): Role
}