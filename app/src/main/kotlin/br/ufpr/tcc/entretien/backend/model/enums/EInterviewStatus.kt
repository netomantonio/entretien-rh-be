package br.ufpr.tcc.entretien.backend.model.enums

enum class EInterviewStatus {
    WAITING_CANDIDATE,
    WAITING_CANDIDATE_REGISTRATION,
    TO_BE_SCHEDULE,
    SCHEDULE,
    IN_PROGRESS,
    ABSENT_CANDIDATE,
    ABSENT_RECRUITER,
    CONCLUDED,
    DID_NOT_OCCUR,
    OTHER
}