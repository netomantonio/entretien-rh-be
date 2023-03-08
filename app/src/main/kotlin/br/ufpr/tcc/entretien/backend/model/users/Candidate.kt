package br.ufpr.tcc.entretien.backend.model.users

import br.ufpr.tcc.entretien.backend.model.Resume
import javax.persistence.*

/**
 * Entity class that models Candidate-specific attributes.
 */
@Entity
class Candidate(
    var socialNetworkig: String? = null,
    var pcd: Boolean = false,
    var cep: String? = null,
    @OneToOne(cascade=[CascadeType.ALL])
    @JoinColumn(name = "fk_resume")
    var resume: Resume? = null,
) : User()