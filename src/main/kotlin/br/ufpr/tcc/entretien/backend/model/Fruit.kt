package br.ufpr.tcc.entretien.backend.model

import java.time.Instant
import javax.persistence.*

@Table(name = "fruit")
@Entity
class Fruit(
    @Column(name = "name") val name: String,
    @Column(name = "createdAt") var createdAt: Instant = Instant.now()
) : AbstractJpaPersistable<Long>()