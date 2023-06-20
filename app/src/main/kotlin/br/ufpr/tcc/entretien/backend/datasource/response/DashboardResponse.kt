package br.ufpr.tcc.entretien.backend.datasource.response

import br.ufpr.tcc.entretien.backend.model.enums.InterviewStatusTypes
import br.ufpr.tcc.entretien.backend.model.interview.Interview
import java.time.LocalDateTime

open class DashboardResponse {
    lateinit var lastUpdate: LocalDateTime

    class InterviewListItem(
        var date: LocalDateTime?,
        val company: String,
        val recruiter: String,
        val candidate: String,
        val interviewStatus: InterviewStatusTypes
    )

    class InterviewsStats(
        var scheduled: Long,
        var toBeScheduled: Long,
        var completed: Long,
        var total: Long
    )

    companion object {
        fun fromInterview(interview: Interview): InterviewListItem {
            return InterviewListItem(
                interview.startingAt,
                interview.manager.companyName,
                (interview.recruiter?.firstName + interview.recruiter?.lastName),
                (interview.candidate?.firstName + interview.candidate?.lastName),
                interview.interviewStatus
            )
        }
    }
}
