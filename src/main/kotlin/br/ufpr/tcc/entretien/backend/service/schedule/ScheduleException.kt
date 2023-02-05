package br.ufpr.tcc.entretien.backend.service.schedule

class ScheduleException(
    val type: ScheduleExceptionType,
    message: String
): Exception(message)