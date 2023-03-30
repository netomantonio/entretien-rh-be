package br.ufpr.tcc.entretien.backend.repository

import br.ufpr.tcc.entretien.backend.model.interview.Interview
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.Optional

interface InterviewRepository : CrudRepository<Interview, Long> {

    fun existsByCandidateId(candidateId: Long): Boolean

    fun existsByCandidateCpf(candidateCpf: String): Boolean

    @Query("SELECT i FROM Interview i WHERE i.cpf = :candidateCpf AND i.interviewStatus = 'WAITING_CANDIDATE_REGISTRATION'")
    fun findByCandidateCpfWithPendingRegistration(@Param(value = "candidateCpf") candidateCpf: String): Optional<Interview>

    fun findByCpf(cpf: String): Optional<Interview>

    fun findByCandidateId(candidateId: Long): Optional<Interview>

    fun findByRecruiterId(recruiterId: Long): Optional<Iterable<Interview>>
}