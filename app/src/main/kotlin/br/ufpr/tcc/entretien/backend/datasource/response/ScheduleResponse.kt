package br.ufpr.tcc.entretien.backend.datasource.response

import br.ufpr.tcc.entretien.backend.datasource.response.enums.DaysOfTheWeekResponse
import br.ufpr.tcc.entretien.backend.model.Schedule
import java.time.LocalTime

data class ScheduleResponse(
    val id: Long,
    val available: Boolean,
    val dayOfTheWeek: String,
    val startingAt: LocalTime,
    val endingAt: LocalTime
)

fun Schedule.toResponse(): ScheduleResponse {
    return ScheduleResponse(
        id = this.getId(),
        available = this.available,
        dayOfTheWeek = DaysOfTheWeekResponse.fromCode(this.dayOfTheWeek.value)!!.description,
        startingAt = this.startingAt,
        endingAt = this.endingAt
    )
}
