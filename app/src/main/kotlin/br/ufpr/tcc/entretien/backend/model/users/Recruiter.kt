package br.ufpr.tcc.entretien.backend.model.users

import br.ufpr.tcc.entretien.backend.model.Schedule
import com.fasterxml.jackson.annotation.JsonManagedReference
import javax.persistence.*
import javax.validation.constraints.NotBlank

/**
 * Entity class that models Recruiter-specific attributes.
 */
@Entity
class Recruiter(
    @NotBlank
    var professionalRecord: String = "",
    var presentation: String? = null,
    var cnpj: String? = null,
    var specialities: String? = null,
    // TODO:
    @OneToMany(cascade=[CascadeType.ALL], mappedBy = "recruiter")
    @JsonManagedReference
    var schedule: MutableSet<Schedule>? = null
) : User()