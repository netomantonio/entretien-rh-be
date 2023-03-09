package br.ufpr.tcc.entretien.backend.controller

import io.openvidu.java.client.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.annotation.PostConstruct

@RestController
@RequestMapping("/api")
class OpenViduController {

    @Value("\${OPENVIDU.url}")
    private lateinit var OPENVIDU_URL: String

    @Value("\${OPENVIDU.secret}")
    private lateinit var OPENVIDU_SECRET: String

    private lateinit var openvidu: OpenVidu

    @PostConstruct
    fun init() {
        this.openvidu = OpenVidu(OPENVIDU_URL, OPENVIDU_SECRET)
    }

    @Throws(OpenViduJavaClientException::class, OpenViduHttpException::class)
    @PostMapping("/sessions")
    fun initializeSession(
            @RequestBody(required = false) params: Map<String, Any>
    ): ResponseEntity<String> {

        val properties: SessionProperties = SessionProperties.fromJson(params).build()
        val session: Session = openvidu.createSession(properties)

        return ResponseEntity(session.sessionId, HttpStatus.OK)
    }

    @Throws(OpenViduJavaClientException::class, OpenViduHttpException::class)
    @PostMapping("/sessions/{sessionId}/connections")
    fun createConnection(
            @PathVariable("sessionId") sessionId: String,
            @RequestBody(required = false) params: Map<String, Any>
    ): ResponseEntity<String> {

        val session = openvidu.getActiveSession(sessionId)
                ?: return ResponseEntity(HttpStatus.NOT_FOUND)

        val properties = ConnectionProperties.fromJson(params).build()
        val connection = session.createConnection(properties)

        return ResponseEntity(connection.token, HttpStatus.OK)
    }

//    @GetMapping("/sessions)
//            fun retrieveSessions(): ResponseEntity<String> {
//                openvidu.
//            }

    @GetMapping
    fun helloWorld(): String = "-> Working REST endpoint."
}
