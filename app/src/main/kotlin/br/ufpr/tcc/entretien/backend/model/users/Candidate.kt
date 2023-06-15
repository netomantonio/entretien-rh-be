package br.ufpr.tcc.entretien.backend.model.users

import br.ufpr.tcc.entretien.backend.model.resume.Resume
import com.fasterxml.jackson.annotation.JsonManagedReference
import javax.persistence.*

/**
 * Entity class that models Candidate-specific attributes.
 */
@Entity
class Candidate(
    var socialNetworking: String? = null,
    var pcd: Boolean = false,
    var cep: String? = null
) : User() {
    @OneToOne(mappedBy = "candidate")
    @JsonManagedReference
    lateinit var resume: Resume
}