package br.ufpr.tcc.entretien.backend.common.error

/**
 * @param Code The available code range for business errors is 700 to 799
 * **/
enum class BusinessErrorCode(val code: String, val description: String) {
    UNAVAILABLE_TIME("UT", "Horário não está mais disponível, tente outro horário"),
    ;
}