package br.ufpr.tcc.entretien.backend.repository

import br.ufpr.tcc.entretien.backend.model.resume.Resume
import org.springframework.data.repository.CrudRepository
import java.util.*

interface ResumeRepository : CrudRepository<Resume, Long> {

    fun findByCandidateId(id: Long): Optional<Resume>
}