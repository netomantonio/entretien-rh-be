package br.ufpr.tcc.entretien.backend.controller

import br.ufpr.tcc.entretien.backend.service.FruitService
import br.ufpr.tcc.entretien.backend.model.Fruit
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import javax.annotation.PostConstruct

@RestController
@RequestMapping("/api")
class FruitController {

    @Autowired
    lateinit var fruitService: FruitService

    @ExceptionHandler(RuntimeException::class, DataIntegrityViolationException::class)
    fun handleBadRequest(e: RuntimeException): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.BAD_REQUEST)

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(e: NoSuchElementException): ResponseEntity<String> =
        ResponseEntity(e.message, HttpStatus.NOT_FOUND)

    @GetMapping("/fruit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    fun getFruitById(@PathVariable id: Long): Fruit = fruitService.getById(id)

    @GetMapping("/fruit/name/{name}")
    @PreAuthorize("hasRole('ADMIN')")
    fun getFruitByName(@PathVariable name: String): Fruit = fruitService.getByName(name)

    @GetMapping("/fruits")
    @PreAuthorize("hasRole('ADMIN')")
    fun getAllFruits(): Iterable<Fruit> = fruitService.getAll()

    @PostMapping("/fruit")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    fun saveFruit(@RequestBody fruit: Fruit) = fruitService.save(fruit)

    @PostMapping("/fruits")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    fun saveFruits(@RequestBody fruits: List<Fruit>) = fruitService.save(fruits)

    @PutMapping("/fruit")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    fun updateFruit(@RequestBody fruit: Fruit) = fruitService.update(fruit)

    @DeleteMapping("/fruit/name/{name}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    fun removeFruitByName(@PathVariable name: String) = fruitService.remove(name)

    @DeleteMapping("/fruit/{id}")
    // @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('ADMIN')")
    fun removeFruitById(@PathVariable id: Long) = fruitService.remove(id)

    @GetMapping("/public")
    fun publicContent() = "public content"
}