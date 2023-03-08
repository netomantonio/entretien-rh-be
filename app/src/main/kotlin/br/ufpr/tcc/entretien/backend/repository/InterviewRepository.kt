package br.ufpr.tcc.entretien.backend.repository

import br.ufpr.tcc.entretien.backend.model.interview.Interview
import org.springframework.data.repository.CrudRepository
import java.util.Optional

interface InterviewRepository : CrudRepository<Interview, Long> {

    fun existsByCandidateId(candidateId: Long): Boolean

    fun findByCandidateId(candidateId: Long): Optional<Interview>

    fun findByRecruiterId(recruiterId: Long): Optional<Iterable<Interview>>
}