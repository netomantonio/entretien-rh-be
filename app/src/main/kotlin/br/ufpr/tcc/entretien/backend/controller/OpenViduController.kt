package br.ufpr.tcc.entretien.backend.controller

import br.ufpr.tcc.entretien.backend.common.exception.interview.UserIsNotAuthorizedException
import br.ufpr.tcc.entretien.backend.common.logger.LOGGER
import br.ufpr.tcc.entretien.backend.service.InterviewService
import br.ufpr.tcc.entretien.backend.service.UserDetailsImpl
import io.openvidu.java.client.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import javax.annotation.PostConstruct
import javax.validation.Valid

@CrossOrigin(origins = ["*"])
@RestController
class OpenviduController {

    companion object {
        private const val LOG_TAG = "entretien-backend-openvidu-controller"
        private val logger = LOGGER.getLogger(OpenviduController::class.java)
    }

    @Value("\${OPENVIDU.url}")
    private val OPENVIDU_URL: String? = null

    @Value("\${OPENVIDU.secret}")
    private val OPENVIDU_SECRET: String? = null

    private var openvidu: OpenVidu? = null

    @Autowired
    lateinit var interviewService: InterviewService

    @PostConstruct
    fun init() {
        openvidu = OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET)
    }

    /**
     * @param params The Session properties
     * @return The Session ID
     */
    @PreAuthorize("hasRole('ROLE_RECRUITER') or hasRole('ROLE_CANDIDATE')")
    @PostMapping("/openvidu/api/sessions/{interviewId}")
    @Throws(OpenViduJavaClientException::class, OpenViduHttpException::class)
    fun initializeSession(
        @Valid
        @PathVariable("interviewId") interviewId: String?,
        @RequestBody(required = false) params: Map<String?, Any?>?,
        authentication: Authentication
    ): ResponseEntity<String?>? {
        try {
            val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
            val interview =
                interviewService.getInterview(interviewId!!.toLong()).filter { it.candidate?.id == userDetails.getId() || it.recruiter?.id == userDetails.getId() }
                    .orElseThrow()
            logger.info(
                LOG_TAG,
                "received request for create a session for the interview by user",
                mapOf("itnerview-id" to interviewId, "user-id" to userDetails.getId().toString())
            )
            if (interview.sessionId?.isNotEmpty() == true) return ResponseEntity(interview.sessionId!!, HttpStatus.OK)
            val properties = SessionProperties.fromJson(params).build()
            val session = openvidu!!.createSession(properties)
            interview.sessionId = session.sessionId
            interviewService.updateInterview(interview)
            return ResponseEntity(session.sessionId, HttpStatus.OK)
        } catch (ex: NoSuchElementException) {
            throw UserIsNotAuthorizedException(ex)
        } catch (ex: IllegalArgumentException) {
            throw IllegalArgumentException(ex)
        } catch (ex: Exception) {
            logger.error(LOG_TAG, "unexpected error", ex.stackTrace)
            throw Exception(ex)
        }
    }

    /**
     * @param sessionId The Session in which to create the Connection
     * @param params    The Connection properties
     * @return The Token associated to the Connection
     */
    @PreAuthorize("hasRole('ROLE_RECRUITER') or hasRole('ROLE_CANDIDATE')")
    @PostMapping("/openvidu/api/sessions/{sessionId}/connections")
    @Throws(
        OpenViduJavaClientException::class,
        OpenViduHttpException::class
    )
    fun createConnection(
        @PathVariable("sessionId") sessionId: String?,
        @RequestBody(required = false) params: Map<String?, Any?>?,
        authentication: Authentication
    ): ResponseEntity<String?>? {
        val session = openvidu!!.getActiveSession(sessionId)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)
        val properties = ConnectionProperties.fromJson(params).build()
        val connection = session.createConnection(properties)
        return ResponseEntity(connection.token, HttpStatus.OK)
    }
}