package br.ufpr.tcc.entretien.backend.model.interview

enum class EInterviewStatus {
    WAITING_CANDIDATE,
    SCHEDULE,
    IN_PROGRESS,
    ABSENT_CANDIDATE,
    ABSENT_RECRUITER,
    CONCLUDED,
    DID_NOT_OCCUR,
    OTHER
}