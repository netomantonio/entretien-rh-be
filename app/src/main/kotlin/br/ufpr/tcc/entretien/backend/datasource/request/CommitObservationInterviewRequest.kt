package br.ufpr.tcc.entretien.backend.datasource.request

import br.ufpr.tcc.entretien.backend.model.enums.InterviewStatusTypes
import org.hibernate.validator.constraints.Range
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Positive
import javax.validation.constraints.Size

data class CommitObservationInterviewRequest(
    @field:NotBlank
    @field:Size(min = 240)
    val candidateObservation: String? = null,
    @field:NotBlank
    @field:Size(min = 240)
    val managerObservation: String? = null,
    @field:Positive
    @field:Range(min = 1L, max = 5L)
    val score: Long? = null,
    @field:NotNull
    val interviewStatus: InterviewStatusTypes? = null
)
