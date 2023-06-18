package br.ufpr.tcc.entretien.backend.datasource.request

import org.hibernate.validator.constraints.Range
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Positive

data class CommitObservationInterviewRequest(
    @field:NotBlank
    val candidateObservation: String,
    @field:NotBlank
    val managerObservation: String,
    @field:Positive
    @field:Range(min = 1L, max = 5L)
    val score: Long
)
