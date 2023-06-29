package br.ufpr.tcc.entretien.backend.controller

import br.ufpr.tcc.entretien.backend.common.logger.LOGGER
import br.ufpr.tcc.entretien.backend.common.utils.toDate
import br.ufpr.tcc.entretien.backend.datasource.request.AdminUpdateRequest
import br.ufpr.tcc.entretien.backend.datasource.request.SignupRequest
import br.ufpr.tcc.entretien.backend.model.users.Admin
import br.ufpr.tcc.entretien.backend.service.UserDetailsImpl
import br.ufpr.tcc.entretien.backend.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import javax.validation.Valid

// TODO: review
@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/user")
class UserController {
    companion object {
        private const val LOG_TAG = "entretien-backend-user-controller"
        private val logger = LOGGER.getLogger(UserController::class.java)
    }

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

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin/dashboard")
    fun getAdminDashboard(
        @RequestParam(value = "from") @DateTimeFormat(pattern = "yyyy-MM-dd")
        from: LocalDate,
        @RequestParam(value = "to") @DateTimeFormat(pattern = "yyyy-MM-dd")
        to: LocalDate,
        authentication: Authentication
    ): ResponseEntity<Any> {
        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val id = userDetails.getId()

        return ResponseEntity.ok<Any>(userService.getDashboard(id, from, to))
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    fun getMe(
        authentication: Authentication
    ): ResponseEntity<Admin> {
        logger.info(LOG_TAG, "getMe")
        try {
            val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
            val adminId = userDetails.getId()
            logger.info(LOG_TAG, "received request from user", mapOf("user-id" to adminId.toString()))
            val me = userService.getAdminById(adminId)
            return ResponseEntity.ok(me)
        } catch (ex: Exception) {
            logger.error(LOG_TAG, ex.message, ex.stackTrace)
            throw IllegalArgumentException()
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping
    fun updated(
        @Valid @RequestBody adminUpdateRequest: AdminUpdateRequest,
        authentication: Authentication
    ): ResponseEntity<String?>? {
        val userDetails: UserDetailsImpl = authentication.principal as UserDetailsImpl
        val adminId = userDetails.getId()
        logger.info(LOG_TAG, "receive request update admin user", mapOf("user-id" to adminId.toString()))
        try {
            val adminModel = userService.getAdminById(adminId)
            val adminUpdated = adminUpdateRequest.toModelAdmin(adminModel)
            userService.update(adminUpdated)
            return ResponseEntity(HttpStatus.OK)
        } catch (ex: Exception) {
            throw Exception()
        }
    }

}

private fun AdminUpdateRequest.toModelAdmin(adminModel: Admin): Admin {
    adminModel.firstName = this.firstName
    adminModel.firstName = this.firstName
    adminModel.lastName = this.lastName
    adminModel.phone = this.phone
    adminModel.birthDay = this.birthDay.toDate()
    return adminModel
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

