package br.ufpr.tcc.entretien.backend.service.openvidu

import br.ufpr.tcc.entretien.backend.common.logger.LOGGER
import br.ufpr.tcc.entretien.backend.model.interview.Interview
import br.ufpr.tcc.entretien.backend.model.openvidu.VideoCallAccess
import br.ufpr.tcc.entretien.backend.repository.InterviewRepository
import io.openvidu.java.client.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Service
import java.util.*

@Service
class CreateRoomService(
    @Value("\${OPENVIDU.url}")
    private val openviduUrl: String,
    @Value("\${OPENVIDU.secret}")
    private val openviduSecret: String
) {
    companion object {
        private const val LOG_TAG = "entretien-backend-create-room-service"
        private val logger = LOGGER.getLogger(CreateRoomService::class.java)
    }

    @Autowired
    lateinit var interviewRepository: InterviewRepository

    private val openviduClient = OpenVidu(openviduUrl, openviduSecret)

    fun execute(
        interview: Interview
    ) {
        try {
            val roomCodeAccess = UUID.randomUUID().toString()
            logger.info(
                LOG_TAG,
                "started the creation of the call room for the interview",
                mapOf(
                    "interview" to interview.getId().toString(),
                    "room" to roomCodeAccess,
                    "candidate" to interview.candidate!!.id.toString(),
                    "recruiter" to interview.recruiter!!.id.toString()
                )
            )
            val (publisherToken, moderatorToken) = configureVideoCall(roomCodeAccess, interview)

            val openviduAccess = VideoCallAccess(
                candidateVideoCallToken = publisherToken,
                recruiterVideoCallToken = moderatorToken,
                roomName = roomCodeAccess
            )

            interview.videoCallAccess = openviduAccess

            interviewRepository.save(interview)

            logger.info(LOG_TAG, "interview room created and information successfully saved")

        } catch (ex: Exception) {
            when(ex) {
                //TODO("In the future add a retry strategy when there is instability in external APIs")
                is OpenViduJavaClientException, is OpenViduHttpException -> {
                    logger.error(LOG_TAG, "error connecting to openVidu server", ex.stackTrace)
                }
                is IllegalArgumentException,  is OptimisticLockingFailureException -> {
                    logger.error(LOG_TAG, "error saving interview room information", ex.stackTrace)
                }
                else -> {
                    logger.error(LOG_TAG, "Unexpected error creating video conference room for interview", ex.stackTrace)
                }
            }

        }
    }

    private fun configureVideoCall(
        roomCodeAccess: String,
        interview: Interview
    ): Pair<String, String> {
        val sessionProperties = generateSessionProperties(customSessionId = roomCodeAccess)

        val session = openviduClient.createSession(sessionProperties)

        val publisherProperties = generateConnectionProperties(id = interview.candidate!!.id)

        val moderatorProperties = generateConnectionProperties(
            id = interview.recruiter!!.id,
            role = OpenViduRole.MODERATOR
        )

        val publisherToken = getToken(session = session, userProperties = publisherProperties)
        val moderatorToken = getToken(session = session, userProperties = moderatorProperties)
        return Pair(publisherToken, moderatorToken)
    }

    private fun getToken(
        session: Session,
        userProperties: ConnectionProperties
    ): String = session.createConnection(userProperties).token

    private fun generateSessionProperties(customSessionId: String): SessionProperties {
        return SessionProperties.Builder()
            .customSessionId(customSessionId)
            .build()
    }

    private fun generateConnectionProperties(
        id: Long,
        type: ConnectionType = ConnectionType.WEBRTC,
        role: OpenViduRole = OpenViduRole.PUBLISHER
    ): ConnectionProperties {
        return ConnectionProperties.Builder()
            .type(type)
            .role(role)
            .data("role:${type.name},userId:${id}")
            .build()
    }
}