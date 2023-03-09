package br.ufpr.tcc.entretien.backend.repository

import br.ufpr.tcc.entretien.backend.model.Fruit
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import java.util.*


interface FruitRepository : CrudRepository<Fruit, Long> {
    fun findByName(fruitName: String): Optional<Fruit>

    @Modifying
    @Query("update Fruit f set f.name = :name where f.id = :id")
    fun updateName(@Param(value = "id") id: Long, @Param(value = "name") name: String)

    fun deleteByName(fruitName: String)
}