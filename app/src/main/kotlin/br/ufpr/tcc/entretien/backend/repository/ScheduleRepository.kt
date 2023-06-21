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
        value = "select s.* from public.schedule as s left join(select * from public.interview i where i.interview_status = 'SCHEDULE' and i.starting_at >= :from and i.starting_at < :to) as i on i.fk_schedule = s.id where i.id is null;"
    )
    fun getAvailableWithinPeriod(@Param("from") from: Timestamp, @Param("to") to: Timestamp): Iterable<Schedule>

    @Transactional
    @Modifying
    @Query("DELETE FROM Schedule s WHERE s.recruiter.id = :id")
    fun removeAllByRecruiterId(@Param(value = "id") id: Long)


    @Query(
        nativeQuery = true,
        value = "select s.* from public.schedule s where s.fk_recruiter = :id order by updated_at LIMIT 1")
    fun getLastByRecruiter(@Param(value = "id") id: Long): Optional<Schedule>

    @Query(value = "select count(s) from Schedule s")
    fun getAllQtd(): Long
}