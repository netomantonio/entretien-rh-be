package br.ufpr.tcc.entretien.backend.datasource.request

import com.fasterxml.jackson.annotation.JsonFormat

open class UpdateUserDataRequest(
    var username: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var phone: String = "",
    var email: String = "",
    @JsonFormat(pattern = "yyyy-MM-dd")
    var birthDay: String? = null
)