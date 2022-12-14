package br.ufpr.tcc.entretien.backend.controller

import br.ufpr.tcc.entretien.backend.service.FruitService
import br.ufpr.tcc.entretien.backend.model.Fruit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.annotation.PostConstruct

@RestController
@RequestMapping("/api")
class FruitController {

    @Autowired
    lateinit var fruitService: FruitService

    @PostConstruct
    fun init() {
    }

    @GetMapping("fruit/{fruitId}")
    fun getFruitById(@PathVariable fruitId: Long): Fruit = fruitService.getFruit(fruitId)

    @GetMapping("/public/info")
    fun getPublicInfo(): ResponseEntity<String> = ResponseEntity.ok("public info")

    @GetMapping("/user/info")
    @PreAuthorize("hasRole('USER')")
    fun getUserInfo(): ResponseEntity<String> = ResponseEntity.ok("user info")

    @GetMapping("/admin/info")
    @PreAuthorize("hasRole('ADMIN')")
    fun getAdminInfo(): ResponseEntity<String> = ResponseEntity.ok("admin info")
}