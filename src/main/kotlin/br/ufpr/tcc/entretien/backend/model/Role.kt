package br.ufpr.tcc.entretien.backend.model

import br.ufpr.tcc.entretien.backend.model.enums.ERole
import javax.persistence.*

@Entity
@Table(name = "roles")
class Role (
    @Column(nullable = false) @Enumerated(EnumType.STRING) val name: ERole
): AbstractJpaPersistable<Long>()