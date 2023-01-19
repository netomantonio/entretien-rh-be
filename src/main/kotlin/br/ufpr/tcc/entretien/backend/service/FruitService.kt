package br.ufpr.tcc.entretien.backend.service

import br.ufpr.tcc.entretien.backend.model.Fruit
import br.ufpr.tcc.entretien.backend.repository.FruitRepository
import org.hibernate.exception.ConstraintViolationException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.HttpClientErrorException.Conflict
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger


@Transactional
@Service
class FruitService(
    TAG: String = "FRUIT_SERVICE"
) {
    var logger: Logger = Logger.getLogger(FruitService::class.java.name)

    @Autowired
    private lateinit var fruitRepository: FruitRepository

    init {
        logger.log(Level.FINE, "$TAG: init")
        println("[LOG] $TAG: init")
    }

    @Throws(RuntimeException::class)
    fun getById(fruitId: Long): Fruit {
        val fruitOpt: Optional<Fruit> = fruitRepository.findById(fruitId)
        if (!fruitOpt.isPresent) {
            throw RuntimeException("Fruit could not be found with id : $fruitId")
        }
        return fruitOpt.get()
    }

    @Throws(NoSuchElementException::class)
    fun getByName(fruitName: String): Fruit {
        val fruitOpt: Optional<Fruit> = fruitRepository.findByName(fruitName)
        if (!fruitOpt.isPresent) {
            throw NoSuchElementException("Fruit could not be found with name: $fruitName")
        }
        return fruitOpt.get()
    }
    fun getAll(): Iterable<Fruit> = fruitRepository.findAll()

    @Throws(DataIntegrityViolationException::class)
    fun save(fruit: Fruit) = fruitRepository.save(fruit)

    fun save(fruits: List<Fruit>) = fruitRepository.saveAll(fruits)

    fun update(fruit: Fruit) {
        fruit.getId()?.let { fruitRepository.updateName(it, fruit.name) }
    }

    fun remove(name: String) = fruitRepository.deleteByName(name)

    fun remove(id: Long) = fruitRepository.deleteById(id)
}