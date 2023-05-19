package br.ufpr.tcc.entretien.backend.repository

import br.ufpr.tcc.entretien.backend.model.Schedule
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import java.util.*

@Repository
interface ScheduleRepository : CrudRepository<Schedule, Long> {

    @Query("SELECT s FROM Schedule s WHERE s.recruiter.id = :id")
    fun getAllByRecruiterId(@Param(value = "id") id: Long): Optional<Iterable<Schedule>>

    @Query(
        nativeQuery = true,
        value = "select * from public.schedule s left join (select * from public.interview i where i.interview_status = 'SCHEDULE' and i.starting_at >= '2020-01-01 00:00:00.000' and i.starting_at <= '2024-01-01 00:00:00.000') i on i.fk_schedule = s.id where i.id is null;"
    )
    fun getAvailableWithinPeriod(@Param("from") from: Timestamp, @Param("to") to: Timestamp): Iterable<Schedule>

    @Transactional
    @Modifying
    @Query("DELETE FROM Schedule s WHERE s.recruiter.id = :id")
    fun removeAllByRecruiterId(@Param(value = "id") id: Long)
}