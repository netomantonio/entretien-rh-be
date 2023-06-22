package br.ufpr.tcc.entretien.backend.datasource.response

import br.ufpr.tcc.entretien.backend.model.interview.Interview

class DashboardAdminResponse : DashboardResponse(
) {
    lateinit var thisMonthScheduledInterviews: List<Interview>
    lateinit var interviewProblemHistory: List<Interview>
    lateinit var interviewsStats: AdminStats

    class AdminStats : InterviewsStats(scheduledQtd = 0, toBeScheduledQtd = 0, completed = 0, total = 0) {
        var candidatesQtd: Long = 0
        var managersQtd: Long = 0
        var recrutiersQtd: Long = 0
        var schedulesQtd: Long = 0
        var unregistredCpfQtd: Long = 0
        var interviewsAbsentCandidateQtd: Long = 0
        var interviewsAbsentRecruiterQtd: Long = 0
        var interviewsDidNotOccur: Long = 0
    }
}
