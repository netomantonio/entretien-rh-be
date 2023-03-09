package br.ufpr.tcc.entretien.backend.controller

import br.ufpr.tcc.entretien.backend.datasource.request.SignupRequest
import br.ufpr.tcc.entretien.backend.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

// TODO: review
@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/user")
class UserController {

    @Autowired
    lateinit var userService: UserService

//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("")
    fun registerAdmin(@Valid @RequestBody signupRequest: SignupRequest): ResponseEntity<*> {

        if (signupRequest != null) {
            if (userService.existsByUsername(signupRequest.username)) {
                return ResponseEntity
                    .badRequest()
                    .body<Any>(("Error: Username is already taken!"))
            }
            if (userService.existsByEmail(signupRequest.email)) {
                return ResponseEntity
                    .badRequest()
                    .body<Any>("Error: Email is already in use!")
            }
        }

        val admin = userService.build(signupRequest)

        return try {
            userService.register(admin)
            ResponseEntity.ok<Any>("User registered successfully!")
        } catch (ex: Exception) {
            println("[ERROR] ------------------------------------------")
            println(ex.message)
            ResponseEntity.internalServerError().body("Persistence error.")
        }
    }

    @GetMapping("")
    fun getAllUsers(): ResponseEntity<*> {
        return try {
            val users = userService.getAll()
            ResponseEntity.ok<Any>(users)
        } catch (ex: Exception) {
            println("[ERROR] ------------------------------------------")
            println(ex.message)
            ResponseEntity.internalServerError().body("Persistence error.")
        }
    }

    //    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping
//    fun findAll() =
//        userService.findAll()
//
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/check")
//    fun checking() =
//        userService.check()
//
//    @GetMapping("/{id}")
//    fun findById(@PathVariable id: String) =
//        userService.findById(id)
//
//    @GetMapping("/username/{username}")
//    fun findByUsername(@PathVariable username: String) =
//        userService.findByUsername(username)
//
//    @PostMapping
//    fun create(@RequestBody userRequest: UserRequest): ResponseEntity<URI> {
//        val response = userService.create(userRequest)
//        if (response.status != 201)
//            throw RuntimeException("User was not created")
//        return ResponseEntity.created(response.location).build()
//    }
//
//    @PostMapping("/{userId}/group/{groupId}")
//    fun assignToGroup(
//        @PathVariable userId: String,
//        @PathVariable groupId: String
//    ) {
//        userService.assignToGroup(userId, groupId)
//    }
//
//    @PostMapping("/{userId}/role/{roleName}")
//    fun assignRole(
//        @PathVariable userId: String,
//        @PathVariable roleName: String
//    ) {
//        val role = roleService.findByName(roleName)
//        userService.assignRole(userId, role)
//    }

}