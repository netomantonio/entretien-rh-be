package br.ufpr.tcc.entretien.backend.repository

import br.ufpr.tcc.entretien.backend.model.enums.EEducationLevel
import br.ufpr.tcc.entretien.backend.model.EducationLevel
import org.springframework.data.repository.CrudRepository
import java.util.Optional

interface EducationLevelRepository : CrudRepository<EducationLevel, Long> {
    fun findByName(name: EEducationLevel): Optional<EducationLevel>
}