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
    val username: String,
    val email: String,
    val password: String,
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_roles",
        joinColumns = [JoinColumn(name = "user_id", referencedColumnName = "id")],
        inverseJoinColumns = [JoinColumn(name = "role_id", referencedColumnName = "id")]
    )
    val roles: Set<Role>
//    @Temporal(TemporalType.DATE) val birthDay: Date,
//    val professionalDocument: String
    // TODO attendance time
) : AbstractJpaPersistable<Long>() {
}