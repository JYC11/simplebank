package org.example.simplebank.common.dataAccess

interface BaseCrudRepository<P, K> {
    fun getById(id: K): P?

    fun save(entity: P): P

    fun update(entity: P): P

    fun delete(entity: P)

    fun deleteById(id: K)

    fun existsById(id: K): Boolean

    fun saveAll(entities: Collection<P>): List<P>

    fun updateAll(entities: Collection<P>): List<P>

    fun deleteAll(entities: Collection<P>)
}