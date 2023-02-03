package br.ufpr.tcc.entretien.backend.repository

import br.ufpr.tcc.entretien.backend.model.Schedule
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface ScheduleRepository : CrudRepository<Schedule, Long> {

    @Query("SELECT s FROM Schedule s WHERE s.recruiter.id = :id")
    fun getAllByRecruiterId(@Param(value = "id")id: Long): Optional<Iterable<Schedule>>
}