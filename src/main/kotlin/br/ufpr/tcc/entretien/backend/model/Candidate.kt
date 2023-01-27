package br.ufpr.tcc.entretien.backend.model

import javax.persistence.*

@Entity
class Candidate(
    var socialNetworkig: String = "",
    var pcd: Boolean = false,
    var cep: String = "",
    @OneToOne(cascade=[CascadeType.ALL])
    @JoinColumn(name = "fk_resume")
    var resume: Resume = Resume(),
) : User()