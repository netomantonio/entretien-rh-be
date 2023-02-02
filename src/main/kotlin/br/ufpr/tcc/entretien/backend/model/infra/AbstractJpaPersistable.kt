package br.ufpr.tcc.entretien.backend.model.infra

import org.springframework.data.util.ProxyUtils
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class AbstractJpaPersistable {

    companion object {
        private val serialVersionUID = -5554308939380869754L
    }

    @Id
    @GeneratedValue
    private var id: Long = 0

    fun getId(): Long {
        return id
    }

    fun setId(id: Long) {
        this.id = id
    }

    override fun equals(other: Any?): Boolean {
        other ?: return false

        // TODO: check if cover different subclasses
        if (this === other) return true
        if (javaClass != ProxyUtils.getUserClass(other)) return false

        other as AbstractJpaPersistable

        return if (null == this.getId()) false else this.getId() == other.getId()
    }

    override fun hashCode(): Int {
        return 31
    }

    override fun toString() = "Entity of type ${this.javaClass.name} with id: $id"
}
