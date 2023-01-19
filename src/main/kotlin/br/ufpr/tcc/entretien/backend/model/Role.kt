package br.ufpr.tcc.entretien.backend.model

import javax.persistence.*

@Entity
@Table(name = "roles")
class Role (
    @Column(nullable = false) @Enumerated(EnumType.STRING) val name: ERole
): AbstractJpaPersistable<Long>()