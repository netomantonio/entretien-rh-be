package br.ufpr.tcc.entretien.backend.repository

import br.ufpr.tcc.entretien.backend.model.Schedule
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Repository
interface ScheduleRepository : CrudRepository<Schedule, Long> {

    @Query("SELECT s FROM Schedule s WHERE s.recruiter.id = :id")
    fun getAllByRecruiterId(@Param(value = "id") id: Long): Optional<Iterable<Schedule>>

    @Transactional
    @Modifying
    @Query("DELETE FROM Schedule s WHERE s.recruiter.id = :id")
    fun removeAllByRecruiterId(@Param(value = "id") id: Long)
}