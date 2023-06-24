package br.ufpr.tcc.entretien.backend.common.utils

import java.text.SimpleDateFormat
import java.util.*

fun String.sanitizeDocuments(): String {
    val regex = Regex("[^0-9]")
    return this.replace(regex, "")
}

fun String?.toDate(): Date? {
    val formatter = SimpleDateFormat("yyyy-MM-dd")
    return formatter.parse(this)
}