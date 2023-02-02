package br.ufpr.tcc.entretien.backend.repository

import br.ufpr.tcc.entretien.backend.model.users.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository<T: User> : CrudRepository<T, Long> {
    fun findByUsername(username: String): Optional<User>

    fun existsByUsername(username: String): Boolean

    fun existsByEmail(email: String): Boolean
}