package br.ufpr.tcc.entretien.backend.model

import java.time.Instant
import javax.persistence.*

@Entity
@Table(name = "fruit")
class Fruit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private val id: Long? = null

    @Column(name = "name")
    lateinit var name: String

    @Column(name = "createdAt")
    private val createdAt = Instant.now()
}