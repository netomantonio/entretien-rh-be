package br.ufpr.tcc.entretien.backend.common.error

/**
 * @param Code The available code range for business errors is 700 to 799
 * **/
enum class BusinessErrorCode(val code: String, val description: String) {
    UNAVAILABLE_TIME("UT", "Schedule is no longer available, try another schedule"),
    INVALID_TOKEN("IT", "this token is invalid and cannot be processed")
    ;
}