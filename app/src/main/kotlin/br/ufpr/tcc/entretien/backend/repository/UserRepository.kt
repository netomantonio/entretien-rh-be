package br.ufpr.tcc.entretien.backend.repository

import br.ufpr.tcc.entretien.backend.model.enums.ERole
import br.ufpr.tcc.entretien.backend.model.users.User
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserRepository<T : User> : CrudRepository<T, Long> {
    fun findByUsername(username: String): Optional<T>

    fun findByCpf(cpf: String): Optional<T>

    fun existsByUsername(username: String): Boolean

    fun existsByEmail(email: String): Boolean

    @Query(
        nativeQuery = true,
        value = "select count(u) from public.users u join public.user_roles ur on u.id = ur.role_id join public.roles r on r.id = u.id where r.name = :role")
    fun getUserQtdByRole(@Value("role") role: String): Long
}