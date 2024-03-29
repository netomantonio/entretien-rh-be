package br.ufpr.tcc.entretien.backend.model.users

import br.ufpr.tcc.entretien.backend.model.infra.Role
import com.fasterxml.jackson.annotation.JsonIgnore
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
    open var id: Long = 0,
    open var firstName: String? = null,
    open var lastName: String? = null,
    open var username: String = "",
    @Temporal(TemporalType.DATE) open var birthDay: Date? = null,
    @Temporal(TemporalType.TIMESTAMP) open val createdAt: Date = Date.from(Instant.now()),
    @Temporal(TemporalType.TIMESTAMP) open var updatedAt: Date = Date.from(Instant.now()),
    open var activated: Boolean = false,
    open var phone: String? = null,
    // TODO: (val gender: ENUM)
    @Column(unique = true) open var email: String = "",
    @JsonIgnore open var password: String = "",
    @Column(nullable = false, unique = true) open var cpf: String = "",
    @ManyToMany(fetch = FetchType.LAZY, cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")]
    )
    open var roles: Set<Role> = emptySet()
)