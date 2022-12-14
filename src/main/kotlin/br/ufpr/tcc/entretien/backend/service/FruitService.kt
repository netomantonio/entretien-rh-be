package br.ufpr.tcc.entretien.backend.service

import br.ufpr.tcc.entretien.backend.model.Fruit
import br.ufpr.tcc.entretien.backend.repository.FruitRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*


@Transactional
@Service
class FruitService(
    fruitRepository: FruitRepository
) {
    private val fruitRepository: FruitRepository

    init {
        this.fruitRepository = fruitRepository
    }

    @Throws(RuntimeException::class)
    fun getFruit(fruitId: Long): Fruit {
        val fruitOpt: Optional<Fruit> = fruitRepository.findById(fruitId)
        if (!fruitOpt.isPresent) {
            throw RuntimeException("Orange could not be found with id : $fruitId")
        }
        return fruitOpt.get()
    }
}