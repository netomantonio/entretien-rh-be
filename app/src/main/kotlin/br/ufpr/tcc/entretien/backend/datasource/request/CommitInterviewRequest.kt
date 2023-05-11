package br.ufpr.tcc.entretien.backend.datasource.request

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime

class CommitInterviewRequest(
    @JsonFormat(pattern = "dd-MM-yyyy")
    val dateTime: LocalDateTime,
    val interviewId: Int
)