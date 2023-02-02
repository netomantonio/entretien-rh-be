package br.ufpr.tcc.entretien.backend.model

import br.ufpr.tcc.entretien.backend.model.enums.EInterviewStatus
import br.ufpr.tcc.entretien.backend.model.infra.AbstractJpaPersistable
import javax.persistence.*

@Entity
@Table(name = "interview_status")
class InterviewStatus(
    @Column(nullable = false) @Enumerated(EnumType.STRING) val name: EInterviewStatus
) : AbstractJpaPersistable()