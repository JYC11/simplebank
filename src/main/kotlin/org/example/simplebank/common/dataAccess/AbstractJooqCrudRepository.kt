package org.example.simplebank.common.dataAccess

import org.jooq.Configuration
import org.jooq.DAO
import org.jooq.DSLContext
import org.jooq.UpdatableRecord

// D: DAO type
// R: Record type
// P: POJO type
// K: Key type
abstract class AbstractJooqCrudRepository<D : DAO<R, P, K>, R : UpdatableRecord<R>, P : Any, K : Any>(
    open val dslContext: DSLContext,
    open val configuration: Configuration,
    private val dao: D
) : BaseCrudRepository<P, K> {
    // add get with lock?

    override fun getById(id: K): P? {
        return dao.findById(id)
    }

    override fun getByIdOrRaise(id: K): P {
        return dao.findById(id) ?: throw NoSuchElementException("Not found for id $id")
    }

    override fun save(entity: P): P {
        dao.insert(entity)
        return entity
    }

    override fun update(entity: P): P {
        dao.update(entity)
        return entity
    }

    override fun delete(entity: P) {
        dao.delete(entity)
    }

    override fun deleteById(id: K) {
        val entity = dao.findById(id)
        entity?.let { dao.delete(it) }
    }

    override fun existsById(id: K): Boolean {
        return dao.existsById(id)
    }

    override fun saveAll(entities: Collection<P>): List<P> {
        dao.insert(entities)
        return entities.toList()
    }

    override fun updateAll(entities: Collection<P>): List<P> {
        dao.update(entities)
        return entities.toList()
    }

    override fun deleteAll(entities: Collection<P>) {
        dao.delete(entities)
    }
}