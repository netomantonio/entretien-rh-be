package br.ufpr.tcc.entretien.backend.model

import javax.persistence.*

@Entity
class Candidate(
    var socialNetworkig: String,
    var pcd: Boolean,
    var cep: String,
    @OneToOne
    var resume: Resume,
) : User()