package com.enthetien.model

import java.util.Date
import javax.persistence.*

@Table(name="users")
@Entity
class User (
    @Column(nullable = false) val cpf: String,
    val firstName: String,
    val lastName: String,
    @Temporal(TemporalType.DATE) val birthDay: Date,
    @Column(nullable = false) @Enumerated(EnumType.ORDINAL) val userType: UserType,
    val professionalDocument: String
    // TODO attendance time
): AbstractJpaPersistable<Long>()