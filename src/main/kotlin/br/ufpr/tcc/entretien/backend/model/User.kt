package br.ufpr.tcc.entretien.backend.model

import java.time.Instant
import java.util.Date
import javax.persistence.*

/**
 * Entity class that models User-base attributes for Candidate, Admin, Recruiter and Manager entity classes.
 */
@Table(
    name = "users",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["username", "email"])
    ]
)
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
abstract class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "users_id_seq")
    @SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq")
    var id: Long = 0,
    var firstName: String? = null,
    var lastName: String? = null,
    var username: String = "",
    @Temporal(TemporalType.DATE) val birthDay: Date? = null,
    @Temporal(TemporalType.TIMESTAMP) val createdAt: Date = Date.from(Instant.now()),
    @Temporal(TemporalType.TIMESTAMP) val updatedAt: Date = Date.from(Instant.now()),
    var activated: Boolean = false,
    var phone: String? = null,
    // TODO: (val gender: ENUM)
    var email: String = "",
    var password: String = "",
    @Column(nullable = false) var cpf: String = "",
    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
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
)