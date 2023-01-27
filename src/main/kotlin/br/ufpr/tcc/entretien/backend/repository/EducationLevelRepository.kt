package br.ufpr.tcc.entretien.backend.repository

import br.ufpr.tcc.entretien.backend.model.EEducationLevel
import br.ufpr.tcc.entretien.backend.model.ERole
import br.ufpr.tcc.entretien.backend.model.EducationLevel
import br.ufpr.tcc.entretien.backend.model.Role
import org.springframework.data.repository.CrudRepository
import java.util.Optional

interface EducationLevelRepository : CrudRepository<EducationLevel, Long> {
    fun findByName(name: EEducationLevel): Optional<EducationLevel>
}