package br.ufpr.tcc.entretien.backend.factory.interview

import br.ufpr.tcc.entretien.backend.model.enums.InterviewStatusTypes
import br.ufpr.tcc.entretien.backend.model.interview.Interview
import java.time.LocalDateTime

class FactoryMockInterviews {
    companion object {
        fun generateInterviews(horaAtual: LocalDateTime): List<Interview> {
            val interviews = mutableListOf<Interview>()
            for (i in 1..10) {
                if (i % 2 == 0) {
                    interviews.addAll(listOf(generateInterviewsConditionDidNotOccour(horaAtual)))
                } else {
                    interviews.addAll(listOf(generateInterviewsConditionNotDidNotOccour(horaAtual)))
                }
            }
            return interviews
        }

        fun generateInterviewsDidNotOccour(horaAtual: LocalDateTime): List<Interview> {
            val interviews = mutableListOf<Interview>()
            for (i in 1.. 10) {
                interviews.addAll(listOf(generateInterviewsConditionDidNotOccour(horaAtual)))
            }
            return interviews
        }

        private fun generateInterviewsConditionDidNotOccour(horaAtual: LocalDateTime): Interview {
            return Interview(
                score = 5,
                recruiterObservation = "recruiterObservation",
                managerObservation = "managerObservation",
                candidateObservation = "candidateObservation",
                interviewStatus = InterviewStatusTypes.DID_NOT_OCCUR,
                startingAt = horaAtual.minusDays(1)
            )
        }

        private fun generateInterviewsConditionNotDidNotOccour(horaAtual: LocalDateTime): Interview {
            return Interview(
                score = 5,
                recruiterObservation = "recruiterObservation",
                managerObservation = "managerObservation",
                candidateObservation = "candidateObservation",
                interviewStatus = InterviewStatusTypes.SCHEDULE,
                startingAt = horaAtual.plusDays(1)
            )
        }
    }

}
