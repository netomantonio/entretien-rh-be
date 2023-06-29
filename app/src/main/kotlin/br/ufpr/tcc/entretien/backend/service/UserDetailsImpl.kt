package br.ufpr.tcc.entretien.backend.service

import br.ufpr.tcc.entretien.backend.model.users.User
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import java.util.stream.Collectors


data class UserDetailsImpl(
    private var id: Long, private var username: String, private var email: String, @JsonIgnore
    private var password: String, private var authorities: Collection<GrantedAuthority>
) : UserDetails {
    private val serialVersionUID = 1L

    companion object {
        fun build(user: User): UserDetailsImpl {

            val authorities: List<GrantedAuthority> = user.roles.stream()
                .map { role -> SimpleGrantedAuthority(role.name.name) }
                .collect(Collectors.toList())
            return UserDetailsImpl(
                user.id,
                user.username,
                user.email,
                user.password,
                authorities
            )
        }
    }

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return authorities
    }

    fun getId(): Long {
        return id
    }

    fun getEmail(): String {
        return email
    }

    override fun getPassword(): String {
        return password
    }

    override fun getUsername(): String {
        return username
    }

    override fun isAccountNonExpired(): Boolean {
        return true
    }

    override fun isAccountNonLocked(): Boolean {
        return true
    }

    override fun isCredentialsNonExpired(): Boolean {
        return true
    }

    override fun isEnabled(): Boolean {
        return true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val user = other as UserDetailsImpl
        return Objects.equals(id, user.id)
    }

    fun isAdmin(): Boolean {
        return authorities.toString() == "[ROLE_ADMIN]"
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + authorities.hashCode()
        result = 31 * result + serialVersionUID.hashCode()
        return result
    }
}