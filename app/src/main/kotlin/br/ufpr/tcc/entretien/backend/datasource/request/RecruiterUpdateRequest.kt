package br.ufpr.tcc.entretien.backend.datasource.request

import br.ufpr.tcc.entretien.backend.common.utils.sanitizeNumbers
import br.ufpr.tcc.entretien.backend.common.utils.toDate
import br.ufpr.tcc.entretien.backend.model.users.Recruiter
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull


data class RecruiterUpdateModelRequest(
    @field:NotNull
    val recruiterUpdateRequest: RecruiterUpdateRequest
) {
    fun toModel(recruiterModel: Recruiter): Recruiter {
        recruiterModel.cnpj = this.recruiterUpdateRequest.cnpj?.sanitizeNumbers() ?: recruiterModel.cnpj
        recruiterModel.presentation = this.recruiterUpdateRequest.presentation
        recruiterModel.professionalRecord = this.recruiterUpdateRequest.professionalRecord!!
        recruiterModel.specialities = this.recruiterUpdateRequest.specialities
        recruiterModel.firstName = this.recruiterUpdateRequest.firstName
        recruiterModel.lastName = this.recruiterUpdateRequest.lastName
        recruiterModel.birthDay = this.recruiterUpdateRequest.birthDay.toDate()
        recruiterModel.email = this.recruiterUpdateRequest.email!!
        recruiterModel.phone = this.recruiterUpdateRequest.phone
        
        return recruiterModel
    }
}

data class RecruiterUpdateRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val professionalRecord: String? = null,
    val presentation: String? = null,
    val cnpj: String? = null,
    val specialities: String? = null,
    @field:NotBlank
    val birthDay: String? = null,
)
