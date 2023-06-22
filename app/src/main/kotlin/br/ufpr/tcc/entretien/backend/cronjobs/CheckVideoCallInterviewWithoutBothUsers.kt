package br.ufpr.tcc.entretien.backend.cronjobs

import br.ufpr.tcc.entretien.backend.model.enums.InterviewStatusTypes
import br.ufpr.tcc.entretien.backend.service.InterviewService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class CheckVideoCallInterviewWithoutBothUsers {

    @Autowired
    lateinit var interviewService: InterviewService

    @Scheduled(cron = "0 0 * * * ?")
    fun execute() {
        val horaAtual = LocalDateTime.now()
        val interviews = interviewService.getAll().filter {
            it.startingAt?.plusHours(1)!! < horaAtual &&
            it.interviewStatus == InterviewStatusTypes.SCHEDULE
        }
        interviews.forEach {
            it.interviewStatus = InterviewStatusTypes.DID_NOT_OCCUR
        }
        return interviewService.saveAll(interviews)
    }

}