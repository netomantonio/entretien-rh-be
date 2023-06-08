package br.ufpr.tcc.entretien.backend.model.openvidu

import br.ufpr.tcc.entretien.backend.model.infra.AbstractJpaPersistable
import java.time.Instant
import java.util.*
import javax.persistence.Entity
import javax.persistence.Temporal
import javax.persistence.TemporalType

@Entity
data class VideoCallAccess(
    var candidateVideoCallToken: String,
    var recruiterVideoCallToken: String,
    val roomName: String,
    var roomId: String,
    @Temporal(TemporalType.TIMESTAMP) val createdAt: Date = Date.from(Instant.now()),
    @Temporal(TemporalType.TIMESTAMP) var updatedAt: Date = Date.from(Instant.now()),
) : AbstractJpaPersistable()