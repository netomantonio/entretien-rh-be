package br.ufpr.tcc.entretien.backend.service

import br.ufpr.tcc.entretien.backend.model.User
import br.ufpr.tcc.entretien.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.function.Supplier
import javax.transaction.Transactional

@Service
class UserDetailsServiceImpl: UserDetailsService {

    @Autowired
    lateinit var userRepository: UserRepository

    @Transactional
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails? {
        val user: User = userRepository.findByUsername(username)
            .orElseThrow {UsernameNotFoundException("User Not Found with username: $username")}

        return UserDetailsImpl.build(user)
    }
}