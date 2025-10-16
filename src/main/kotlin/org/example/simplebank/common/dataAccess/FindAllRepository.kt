package org.example.simplebank.common.dataAccess

interface FindAllRepository<P, K> {
    fun findAllByIds(ids: Collection<K>): List<P>
}