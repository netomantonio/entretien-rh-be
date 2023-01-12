package br.ufpr.tcc.entretien.backend.service

import br.ufpr.tcc.entretien.backend.model.User
import com.fasterxml.jackson.annotation.JsonIgnore
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.util.*
import java.util.stream.Collectors


class UserDetailsImpl : UserDetails {
    private val serialVersionUID = 1L

    private var id: Long

    private var username: String

    private var email: String

    @JsonIgnore
    private var password: String

    private var authorities: Collection<GrantedAuthority>

    constructor(
        id: Long, username: String, email: String, password: String,
        authorities: Collection<GrantedAuthority>
    ) {
        this.id = id
        this.username = username
        this.email = email
        this.password = password
        this.authorities = authorities
    }

    companion object {
        fun build(user: User): UserDetailsImpl {

            val authorities: List<GrantedAuthority> = user.roles.stream()
                .map { role -> SimpleGrantedAuthority(role.name.name) }
                .collect(Collectors.toList())
            return UserDetailsImpl(
                user.getId(),
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

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val user = o as UserDetailsImpl
        return Objects.equals(id, user.id)
    }
}