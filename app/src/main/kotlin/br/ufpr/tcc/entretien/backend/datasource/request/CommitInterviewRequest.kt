package br.ufpr.tcc.entretien.backend.datasource.request

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

class CommitInterviewRequest(
    val scheduleId: Long,
    @JsonFormat(pattern = "dd-MM-yyyy")
    val date: LocalDate
)