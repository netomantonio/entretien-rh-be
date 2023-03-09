package br.ufpr.tcc.entretien.backend.model.users

import javax.persistence.*
import javax.validation.constraints.NotBlank

/**
 * Entity class that models Manager-specific attributes.
 */
@Entity
class Manager(
    @NotBlank var companyName: String = "",
    var tradingName: String? = null,
    @NotBlank var cnpj: String = "",
    var operationArea: String? = null,
    var credits: Int = 0
) : User()