package org.example.simplebank.common.dataAccess

import org.jooq.DAO
import org.jooq.DSLContext
import org.jooq.TableField
import org.jooq.UpdatableRecord
import org.jooq.impl.TableImpl

// T: Table type
// D: DAO type
// R: Record type
// P: POJO type
// K: Key type
abstract class AbstractJooqCrudRepository<
        D : DAO<R, P, K>,
        R : UpdatableRecord<R>,
        T : TableImpl<R>,
        P : Any,
        K : Any>(
    open val dslContext: DSLContext,
    private val dao: D,
    private val table: T,
    protected val idField: TableField<R, K?>
) : BaseCrudRepository<P, K> {

    protected fun getOneQuery(id: K) =
        dslContext.selectFrom(table)
            .where(idField.eq(id))

    override fun getById(id: K, lockMode: LockMode): P? {
        return when (lockMode) {
            LockMode.NONE -> getOneQuery(id)
            LockMode.PESSIMISTIC_WRITE -> getOneQuery(id).forUpdate()
            LockMode.PESSIMISTIC_READ -> getOneQuery(id).forShare()
        }.fetchOneInto(dao.type)
    }

    override fun getByIdOrRaise(id: K, lockMode: LockMode): P {
        return getById(id, lockMode) ?: throw NoSuchElementException("Not found for id $id")
    }

    protected fun getAllQuery(ids: Collection<K>) =
        dslContext.selectFrom(table)
            .where(idField.emptyIn(ids))

    override fun getAllByIdsWithLock(ids: Collection<K>, lockMode: LockMode): List<P> {
        return when (lockMode) {
            LockMode.NONE -> getAllQuery(ids)
            LockMode.PESSIMISTIC_WRITE -> getAllQuery(ids).forUpdate()
            LockMode.PESSIMISTIC_READ -> getAllQuery(ids).forShare()
        }.fetchInto(dao.type)
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