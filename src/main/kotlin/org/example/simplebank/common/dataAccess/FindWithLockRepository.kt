package org.example.simplebank.common.dataAccess

interface FindWithLockRepository<P, K> {
    fun findWithLockById(id: K, lockMode: LockMode = LockMode.PESSIMISTIC_WRITE): P?

    fun findWithLockByIdOrRaise(id: K, lockMode: LockMode = LockMode.PESSIMISTIC_WRITE): P

    fun findAllWithLockByIds(ids: Collection<K>, lockMode: LockMode = LockMode.PESSIMISTIC_WRITE): List<P>
}