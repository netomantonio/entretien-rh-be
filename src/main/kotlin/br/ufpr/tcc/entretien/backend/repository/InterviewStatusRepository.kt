package br.ufpr.tcc.entretien.backend.repository

import br.ufpr.tcc.entretien.backend.model.InterviewStatus
import br.ufpr.tcc.entretien.backend.model.enums.EInterviewStatus
import org.springframework.data.repository.CrudRepository
import java.util.Optional

interface InterviewStatusRepository : CrudRepository<InterviewStatus, Long> {
    fun findByName(name: EInterviewStatus): Optional<InterviewStatus>
}