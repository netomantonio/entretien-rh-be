package br.ufpr.tcc.entretien.backend.service

import br.ufpr.tcc.entretien.backend.model.User
import br.ufpr.tcc.entretien.backend.repository.RoleRepository
import br.ufpr.tcc.entretien.backend.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

// TODO
@Service
class AuthService {

    @Autowired
    lateinit var userRepository: UserRepository<User>

    @Autowired
    lateinit var roleRepository: RoleRepository

    fun registerUser(user: User){
        userRepository.save(user)
    }

    fun userAlreadyExists(user: User): Boolean{
        return (userRepository.existsByUsername(user.username) || userRepository.existsByEmail(user.email))
    }
}