package br.ufpr.tcc.entretien.backend.model

import br.ufpr.tcc.entretien.backend.model.infra.AbstractJpaPersistable
import java.time.Instant
import javax.persistence.*

@Table(name = "fruit")
@Entity
class Fruit(
    @Column(unique = true, name = "name") val name: String,
    @Column(name = "createdAt") var createdAt: Instant = Instant.now()
) : AbstractJpaPersistable()