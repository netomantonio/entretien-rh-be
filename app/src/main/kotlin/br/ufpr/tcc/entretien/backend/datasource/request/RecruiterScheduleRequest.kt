package br.ufpr.tcc.entretien.backend.datasource.request

import br.ufpr.tcc.entretien.backend.model.enums.DaysOfTheWeek
import java.time.LocalTime
import javax.validation.constraints.NotBlank

class RecruiterScheduleRequest(
    var agenda: Agenda,
) {
    class Agenda(
        @NotBlank
        var dayOfTheWeek: DaysOfTheWeek,
        var timesOfTheDay: MutableSet<TimeSpan>
    )

    class TimeSpan(
        var startingAt: LocalTime,
        var endingAt: LocalTime
    )
}