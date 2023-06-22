package br.ufpr.tcc.entretien.backend.repository

import br.ufpr.tcc.entretien.backend.model.resume.Resume
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime
import java.util.*

interface ResumeRepository : CrudRepository<Resume, Long> {

    fun findByCandidateId(id: Long): Optional<Resume>

    @Query(value = "select r.updatedAt from Resume r where r.candidate.id = :id")
    fun getCandidateResumeLastUpdate(@Param("id") candidateId: Long): LocalDateTime
}