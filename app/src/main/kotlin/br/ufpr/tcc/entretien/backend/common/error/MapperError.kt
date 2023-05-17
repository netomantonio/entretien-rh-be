package br.ufpr.tcc.entretien.backend.common.error

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class ApiError(val status: String? = null, val message: String, val fieldViolations: List<FieldViolation>? = null)

@JsonInclude(JsonInclude.Include.NON_NULL)
data class FieldViolation(val field: String, val description: String)