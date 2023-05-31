package br.ufpr.tcc.entretien.backend.datasource.request

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDate

class CommitInterviewRequest(
    val scheduleId: Long,
    val interviewId: Long,
    @JsonFormat(pattern = "yyyy-MM-dd")
    val date: LocalDate
)