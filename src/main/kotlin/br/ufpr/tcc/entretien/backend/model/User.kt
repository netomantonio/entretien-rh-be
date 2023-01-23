package br.ufpr.tcc.entretien.backend.model

import java.time.Instant
import java.util.Date
import javax.persistence.*

// TODO: implement diagram's properties
@Table(
    name = "users",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["username", "email"])
    ]
)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
open class User(
    // USER's
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "user_id_seq")
    @SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq")
    var id: Long = 0,
    var firstName: String = "",
    var lastName: String = "",
    var username: String = "",
    @Temporal(TemporalType.DATE) val birthDay: Date = Date.from(Instant.now()),
    @Temporal(TemporalType.TIMESTAMP) val createdAt :Date = Date.from(Instant.now()),
    @Temporal(TemporalType.TIMESTAMP) val updatedAt :Date = Date.from(Instant.now()),
    var activated: Boolean = false,
    var phone: String = "",
    // TODO: (val gender: ENUM)
    var email: String = "",
    var password: String = "",
    @Column(nullable = false) val cpf: String = "",
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")]
    )
    var roles: Set<Role> = emptySet(),
    // MANAGER's
//    var cnpj: String = "",
//    var corporateName: String = "",
//    var tradingName: String = "",
//    var practiceArea: String = "",
//    // RECRUITER's
//    var professionalDocument: String = "",
//    var presentation: String = "",
////    var cnpj: String = ""
//    @ElementCollection
//    var specialties: Set<String> = emptySet()
//    // TODO attendance time
) {
//    constructor(username: String, email: String, password: String) : this(
//        username, email, password, emptySet<Role>()
//    )
}