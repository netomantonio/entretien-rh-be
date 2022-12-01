package br.ufpr.tcc.entretien.backend.model

import javax.persistence.Entity
import javax.persistence.Table

@Table(name="test")
@Entity
class Test(
    val name: String,
) : AbstractJpaPersistable<Long>()