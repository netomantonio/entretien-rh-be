package br.ufpr.tcc.entretien.backend.repository

import br.ufpr.tcc.entretien.backend.model.interview.Interview
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.sql.Timestamp
import java.time.LocalDateTime
import java.util.Optional

interface InterviewRepository : CrudRepository<Interview, Long> {

    fun existsByCandidateId(candidateId: Long): Boolean

    fun existsByCandidateCpf(candidateCpf: String): Boolean

    @Query("SELECT i FROM Interview i WHERE i.cpf = :candidateCpf AND i.interviewStatus = 'WAITING_CANDIDATE_REGISTRATION'")
    fun findByCandidateCpfWithPendingRegistration(@Param(value = "candidateCpf") candidateCpf: String): Optional<Interview>

    fun findByCpf(cpf: String): Optional<Interview>

    fun findByCandidateId(candidateId: Long): Optional<Interview>

    fun findByRecruiterId(recruiterId: Long): Optional<Iterable<Interview>>

    fun findByManagerId(managerId: Long): Optional<Iterable<Interview>>

    @Query(
        nativeQuery = true,
        value = "select i.* from public.interview i where i.interview_status = 'SCHEDULE' and i.starting_at <= :from and i.starting_at >= :to"
    )
    fun findByPeriod(@Param("from") from: Timestamp, @Param("to") to: Timestamp): Optional<Iterable<Interview>>

    @Query(
        nativeQuery = true,
        value = "select i.* from public.interview i where i.starting_at <= :from and i.starting_at >= :to"
    )
    fun findAnyByPeriod(@Param("from") from: Timestamp, @Param("to") to: Timestamp): Optional<Iterable<Interview>>

    fun findAllByCandidateId(id: Long): Optional<List<Interview>>

    @Query(
        "SELECT i FROM Interview i WHERE i.interviewStatus = 'SCHEDULE' AND i.candidate.id = :candidateId AND i.startingAt >= :from AND i.startingAt < :to"
    )
    fun getWithinPeriodByCandidate(
        @Param("candidateId") id: Long,
        @Param("from") from: LocalDateTime,
        @Param("to") to: LocalDateTime
    ): Iterable<Interview>

    @Query(
        nativeQuery = true,
        value = "select i.* from public.interview i where i.fk_candidate = :candidateId and i.starting_at >= :today order by i.starting_at limit 1"
    )
    fun getCandidateNextInterview(@Param("candidateId") candidateId: Long, @Param("today") today: LocalDateTime): Optional<Interview>

    @Query(
        value = "select count(i) from Interview i where i.candidate.id = :candidateId and i.interviewStatus = 'SCHEDULE'"
    )
    fun getCandidateScheduledInterviewsQtd(@Param("candidateId") candidateId: Long): Long

    @Query(
        value = "select count(i) from Interview i where i.candidate.id = :candidateId and i.interviewStatus = 'TO_BE_SCHEDULE'"
    )
    fun getCandidateToBeScheduledInterviewsQtd(@Param("candidateId") candidateId: Long): Long

    @Query(
        value = "select count(i) from Interview i where i.candidate.id = :candidateId"
    )
    fun getCandidateTotalInterviewsQtd(@Param("candidateId") candidateId: Long): Long

    @Query(
        value = "select count(i) from Interview i where i.candidate.id = :candidateId and i.interviewStatus = 'CONCLUDED'"
    )
    fun getCandidateConcludedInterviewsQtd(@Param("candidateId") candidateId: Long): Long
}