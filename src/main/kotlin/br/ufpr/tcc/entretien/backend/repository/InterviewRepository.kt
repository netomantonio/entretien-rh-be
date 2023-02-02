package br.ufpr.tcc.entretien.backend.repository

import br.ufpr.tcc.entretien.backend.model.Interview
import org.springframework.data.repository.CrudRepository

interface InterviewRepository : CrudRepository<Interview, Long> {
}