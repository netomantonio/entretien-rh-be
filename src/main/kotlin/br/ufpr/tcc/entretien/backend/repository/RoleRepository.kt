package br.ufpr.tcc.entretien.backend.repository

import br.ufpr.tcc.entretien.backend.model.enums.ERole
import br.ufpr.tcc.entretien.backend.model.Role
import org.springframework.data.repository.CrudRepository
import java.util.Optional

interface RoleRepository : CrudRepository<Role, Long> {
    fun findByName(name: ERole): Optional<Role>
}