package br.ufpr.tcc.entretien.backend.controller

import br.ufpr.tcc.entretien.backend.datasource.request.UserRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/api/user")
class UserController(
//    private val userService: UserService,
//    private val roleService: RoleService
) {

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