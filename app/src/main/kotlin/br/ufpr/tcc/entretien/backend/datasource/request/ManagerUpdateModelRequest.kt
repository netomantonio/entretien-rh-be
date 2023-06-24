package br.ufpr.tcc.entretien.backend.datasource.request

import br.ufpr.tcc.entretien.backend.common.utils.sanitizeDocuments
import br.ufpr.tcc.entretien.backend.common.utils.toDate
import br.ufpr.tcc.entretien.backend.model.users.Manager
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class ManagerUpdateModelRequest(
    @field:NotNull
    val managerUpdateRequest: ManagerUpdateRequest
) {
    fun toModel(managerModel: Manager): Manager {
        managerModel.tradingName = this.managerUpdateRequest.tradingName!!
        managerModel.companyName = this.managerUpdateRequest.companyName!!
        managerModel.operationArea = this.managerUpdateRequest.operationArea
        managerModel.firstName = this.managerUpdateRequest.firstName
        managerModel.lastName = this.managerUpdateRequest.lastName
        managerModel.birthDay = this.managerUpdateRequest.birthDay.toDate()
        managerModel.cnpj = this.managerUpdateRequest.cnpj?.sanitizeDocuments() ?: managerModel.cnpj
        managerModel.email = this.managerUpdateRequest.email!!
        managerModel.phone = this.managerUpdateRequest.phone

        return managerModel
    }
}

data class ManagerUpdateRequest(
    val firstName: String? = null,
    val lastName: String? = null,
    val phone: String? = null,
    val email: String? = null,
    val cnpj: String? = null,
    val tradingName: String? = null,
    val companyName: String? = null,
    val operationArea: String? = null,
    @field:NotBlank
    val birthDay: String? = null,
)
