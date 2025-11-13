package org.example.simplebank.transaction.infra

import java.util.UUID
import org.example.simplebank.common.dataAccess.LockMode
import org.jooq.DSLContext
import org.jooq.generated.tables.pojos.JTransactionsPojo
import org.springframework.stereotype.Repository

@Repository
class TransactionsRepositoryImpl(
    private val dslContext: DSLContext,
    private val jooqCrudRepository: TransactionsJooqCrudRepository
) : TransactionsRepository {
    override fun getById(id: UUID, lockMode: LockMode) = jooqCrudRepository.getById(id)
    override fun getByIdOrRaise(id: UUID, lockMode: LockMode) = jooqCrudRepository.getByIdOrRaise(id)
    override fun getAllByIdsWithLock(ids: Collection<UUID>, lockMode: LockMode): List<JTransactionsPojo> =
        jooqCrudRepository.getAllByIdsWithLock(ids, lockMode)

    override fun save(entity: JTransactionsPojo) = jooqCrudRepository.save(entity)
    override fun update(entity: JTransactionsPojo) = throw UnsupportedOperationException()
    override fun delete(entity: JTransactionsPojo) = throw UnsupportedOperationException()
    override fun deleteById(id: UUID) = throw UnsupportedOperationException()
    override fun existsById(id: UUID) = jooqCrudRepository.existsById(id)
    override fun saveAll(entities: Collection<JTransactionsPojo>) = jooqCrudRepository.saveAll(entities)
    override fun updateAll(entities: Collection<JTransactionsPojo>) = throw UnsupportedOperationException()
    override fun deleteAll(entities: Collection<JTransactionsPojo>) = throw UnsupportedOperationException()
}