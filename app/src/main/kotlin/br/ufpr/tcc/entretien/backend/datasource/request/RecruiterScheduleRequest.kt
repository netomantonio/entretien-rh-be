package br.ufpr.tcc.entretien.backend.datasource.request

import br.ufpr.tcc.entretien.backend.model.enums.EDayOfTheWeek
import java.time.LocalTime
import javax.validation.constraints.NotBlank

class RecruiterScheduleRequest(
    var agenda: MutableSet<Agenda>,
) {
    class Agenda(
        @NotBlank
        var dayOfTheWeek: EDayOfTheWeek,
        var timesOfTheDay: MutableSet<TimeSpan>
    )

    class TimeSpan(
        var startingAt: LocalTime,
        var endingAt: LocalTime
    )
}