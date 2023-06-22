import br.ufpr.tcc.entretien.backend.cronjobs.CheckVideoCallInterviewWithoutBothUsers
import br.ufpr.tcc.entretien.backend.factory.interview.FactoryMockInterviews
import br.ufpr.tcc.entretien.backend.model.enums.InterviewStatusTypes
import br.ufpr.tcc.entretien.backend.service.InterviewService
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class CheckVideoCallInterviewWithoutBothUsersTest {

    @InjectMocks
    private lateinit var cronjob: CheckVideoCallInterviewWithoutBothUsers

    @Mock
    private lateinit var interviewService: InterviewService


    @Test
    fun executeTest() {
        val horaAtual = LocalDateTime.now()
        // Configuração do cenário de teste
        val interviews = FactoryMockInterviews.generateInterviews(horaAtual)
        val interviewsDidNotOccour = interviews.filter {
            it.startingAt?.plusHours(1)!! < horaAtual &&
                it.interviewStatus == InterviewStatusTypes.SCHEDULE
        }

        // Configurando o comportamento do serviço mockado
        `when`(interviewService.getAll()).thenReturn(interviewsDidNotOccour)

        // Executando o cronjob
        cronjob.execute()

        // Verificando se o método saveAll() do serviço foi chamado com a lista correta de entrevistas
        verify(interviewService, times(1)).saveAll(interviewsDidNotOccour)

        // Verificando se as entrevistas foram atualizadas corretamente
        interviewsDidNotOccour.forEach {
            assertEquals(InterviewStatusTypes.DID_NOT_OCCUR, it.interviewStatus)
        }
    }
}
