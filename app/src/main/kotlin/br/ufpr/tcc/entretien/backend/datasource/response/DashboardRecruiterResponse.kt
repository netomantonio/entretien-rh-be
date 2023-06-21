package br.ufpr.tcc.entretien.backend.datasource.response

import java.time.LocalDateTime

class DashboardRecruiterResponse: DashboardResponse(
) {
    var nextInterview: LocalDateTime? = null
    lateinit var thisMonthScheduledInterviews: List<InterviewListItem>
    lateinit var interviewsHistory: List<InterviewListItem>
    lateinit var interviewsStats: InterviewsStats
}
