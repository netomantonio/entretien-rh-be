package br.ufpr.tcc.entretien.backend.datasource.response

import java.time.LocalDateTime

class RecruiterDashboardResponse: DashboardResponse(
) {
    lateinit var nextInterview: LocalDateTime
    lateinit var thisMonthScheduledInterviews: List<InterviewListItem>
    lateinit var interviewsHistory: List<InterviewListItem>
    lateinit var interviewsStats: InterviewsStats
}
