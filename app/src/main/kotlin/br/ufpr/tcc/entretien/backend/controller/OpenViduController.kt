package br.ufpr.tcc.entretien.backend.controller

import br.ufpr.tcc.entretien.backend.common.exception.interview.UserIsNotAuthorizedException
import br.ufpr.tcc.entretien.backend.common.logger.LOGGER
import br.ufpr.tcc.entretien.backend.model.enums.InterviewStatusTypes
import br.ufpr.tcc.entretien.backend.model.interview.Interview
import br.ufpr.tcc.entretien.backend.service.InterviewService
import br.ufpr.tcc.entretien.backend.service.UserDetailsImpl
import io.openvidu.java.client.*
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.*
import javax.annotation.PostConstruct
import javax.validation.Valid
import kotlin.NoSuchElementException

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
            interviewService.getInterview(interviewId!!.toLong())
                .filter { it.candidate?.id == userDetails.getId() || it.recruiter?.id == userDetails.getId() }
                .orElseThrow()
            logger.info(
                LOG_TAG,
                "received request for create a session for the interview by user",
                mapOf("itnerview-id" to interviewId, "user-id" to userDetails.getId().toString())
            )
            val properties = SessionProperties.fromJson(params).build()
            val session = openvidu!!.createSession(properties)
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
        @Required @PathVariable("sessionId") sessionId: String?,
        @RequestBody(required = false) params: Map<String?, Any?>?,
        authentication: Authentication
    ): ResponseEntity<String?>? {
        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val session = openvidu!!.getActiveSession(sessionId)
            ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        val interview = interviewService.findBySessionId(sessionId!!)

        interviewService.setUserPresent(userDetails, interview)

        val properties = ConnectionProperties.fromJson(params).build()
        val connection = session.createConnection(properties)
        return ResponseEntity(connection.token, HttpStatus.OK)
    }

    /**
     * @param sessionId The Session in which to create the Connection
     * @return User Type Name (String?)
     */
    @PreAuthorize("hasRole('ROLE_RECRUITER') or hasRole('ROLE_CANDIDATE')")
    @PostMapping("/openvidu/api/sessions/{sessionId}/finish")
    @Throws(
        OpenViduJavaClientException::class,
        OpenViduHttpException::class
    )
    fun finishVideoCall(
        @Required @PathVariable("sessionId") sessionId: String?,
        authentication: Authentication
    ): ResponseEntity<String?>? {
        val interview = interviewService.findBySessionId(sessionId!!)
        logger.info(
            LOG_TAG,
            "Starting the validation of rules for ending the video call",
            mapOf("interview-id" to interview.getId().toString())
        )
        return validatedFinishInterview(interview)
    }

    @PreAuthorize("hasRole('ROLE_CANDIDATE')")
    @PostMapping("/openvidu/api/sessions/{sessionId}/newInterview")
    @Throws(
        OpenViduJavaClientException::class,
        OpenViduHttpException::class
    )
    fun createNewInterview(
        @Required @PathVariable("sessionId") sessionId: String?,
        authentication: Authentication
    ): ResponseEntity<String?>? {
        val interview = interviewService.findBySessionId(sessionId!!)
        logger.info(
            LOG_TAG,
            "received a request to create a new interview for a candidate whose recruiter did not attend the interview",
            mapOf("interview-id" to interview.getId().toString())
        )
        val newInterviewSaved = createNewInterview(interview)
        logger.info(
            LOG_TAG,
            "new interview created for candidate whose recruiter did not attend the interview",
            mapOf("new-interview-id" to newInterviewSaved.getId().toString())
        )
        return ResponseEntity(HttpStatus.CREATED)
    }

    private fun createNewInterview(interview: Interview): Interview {
        val newInterview = Interview()
        newInterview.manager = interview.manager
        newInterview.interviewStatus = InterviewStatusTypes.TO_BE_SCHEDULE
        newInterview.candidate = interview.candidate
        newInterview.recruiterObservation = interview.recruiterObservation
        newInterview.sessionId = UUID.randomUUID().toString()

        return interviewService.registerInterview(newInterview)
    }

    private fun validatedFinishInterview(interview: Interview): ResponseEntity<String?> {
        if (InterviewStatusTypes.ABSENT_RECRUITER == interview.interviewStatus ||
            InterviewStatusTypes.ABSENT_CANDIDATE == interview.interviewStatus
        ) {
            logger.info(
                LOG_TAG,
                "This interview no longer allows status changes",
                mapOf("interview-id" to interview.getId().toString())
            )
            throw IllegalArgumentException()
        } else if (interview.candidatePresent == true && interview.recruiterPresent == true) {
            logger.info(
                LOG_TAG,
                "Recruiter and Candidate is present, recruiter continues the flow",
                mapOf("interview-id" to interview.getId().toString())
            )
            return ResponseEntity("Both",HttpStatus.OK)
        } else if (interview.candidatePresent == true && interview.recruiterPresent == false) {
            logger.info(
                LOG_TAG,
                "Candidate is present, created new interview and changed the status of the current one to ABSENT_RECRUITER",
                mapOf("interview-id" to interview.getId().toString())
            )
            interview.interviewStatus = InterviewStatusTypes.ABSENT_RECRUITER
            interviewService.updateInterview(interview)
            logger.info(
                LOG_TAG,
                "successfully changed interview status",
                mapOf("interview-id" to interview.getId().toString())
            )
            return ResponseEntity("Candidate", HttpStatus.OK)
        } else if (interview.recruiterPresent == true && interview.candidatePresent == false) {
            interview.interviewStatus = InterviewStatusTypes.ABSENT_CANDIDATE
            logger.info(
                LOG_TAG,
                "Recruiter is present",
                mapOf("interview-id" to interview.getId().toString())
            )
            interviewService.updateInterview(interview)
            logger.info(
                LOG_TAG,
                "successfully changed interview status",
                mapOf("interview-id" to interview.getId().toString())
            )
            return ResponseEntity("Recruiter", HttpStatus.OK)
        } else throw Exception()
    }
}