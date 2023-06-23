package br.ufpr.tcc.entretien.backend.common.utils

fun String.sanitizeDocuments(): String {
    val regex = Regex("[^0-9]")
    return this.replace(regex, "")
}