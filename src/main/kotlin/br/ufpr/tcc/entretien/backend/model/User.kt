package br.ufpr.tcc.entretien.backend.model

//import java.util.Date
import javax.persistence.*

@Table(
    name = "users",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["username", "email"])
    ]
)
@Entity
data class User(
//    @Column(nullable = false) val cpf: String,
//    val firstName: String,
//    val lastName: String,
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "user_id_seq")
    @SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq")
    var id: Long = 0,
    val username: String,
    val email: String,
    val password: String,
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")]
    )
    var roles: Set<Role>
//    @Temporal(TemporalType.DATE) val birthDay: Date,
//    val professionalDocument: String
    // TODO attendance time
) {

//    constructor(username: String, email: String, password: String) : this(
//        username, email, password, emptySet<Role>()
//    )
}