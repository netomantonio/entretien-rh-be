package br.ufpr.tcc.entretien.backend.repository

import br.ufpr.tcc.entretien.backend.model.Fruit
import org.springframework.data.repository.CrudRepository


interface FruitRepository : CrudRepository<Fruit, Long>