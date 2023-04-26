package br.ufpr.tcc.entretien.backend.datasource.response.enums

enum class DaysOfTheWeekResponse(val code: Int, val description: String) {
    SUNDAY(0, "domingo"),
    MONDAY(1, "segunda-feira"),
    TUESDAY(2, "terça-feira"),
    WEDNESDAY(3, "quarta-feira"),
    THURSDAY(4, "quinta-feira"),
    FRIDAY(5, "sexta-feira"),
    SATURDAY(6, "sábado");

    companion object {
        fun fromCode(code: Int): DaysOfTheWeekResponse? {
            return values().firstOrNull { it.code == code }
        }
    }
}
