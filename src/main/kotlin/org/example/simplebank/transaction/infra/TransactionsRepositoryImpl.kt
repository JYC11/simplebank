package org.example.simplebank.transaction.infra

import java.util.UUID
import org.example.simplebank.common.dataAccess.LockMode
import org.example.simplebank.common.dataAccess.emptyIn
import org.jooq.DSLContext
import org.jooq.generated.tables.pojos.JTransactions
import org.jooq.generated.tables.references.ACCOUNTS
import org.jooq.generated.tables.references.TRANSACTIONS
import org.springframework.stereotype.Repository

@Repository
class TransactionsRepositoryImpl(
    private val dslContext: DSLContext,
    private val jooqCrudRepository: TransactionsJooqCrudRepository
) : TransactionsRepository {
    override fun getById(id: UUID) = jooqCrudRepository.getById(id)
    override fun getByIdOrRaise(id: UUID) = jooqCrudRepository.getByIdOrRaise(id)
    override fun save(entity: JTransactions) = jooqCrudRepository.save(entity)
    override fun update(entity: JTransactions) = throw UnsupportedOperationException()
    override fun delete(entity: JTransactions) = throw UnsupportedOperationException()
    override fun deleteById(id: UUID) = throw UnsupportedOperationException()
    override fun existsById(id: UUID) = jooqCrudRepository.existsById(id)
    override fun saveAll(entities: Collection<JTransactions>) = jooqCrudRepository.saveAll(entities)
    override fun updateAll(entities: Collection<JTransactions>) = throw UnsupportedOperationException()
    override fun deleteAll(entities: Collection<JTransactions>) = throw UnsupportedOperationException()

    private fun getOneQuery(id: UUID) = dslContext.selectFrom(TRANSACTIONS)
        .where(ACCOUNTS.ID.eq(id))

    override fun findWithLockById(id: UUID, lockMode: LockMode): JTransactions? {
        return when (lockMode) {
            LockMode.PESSIMISTIC_WRITE -> getOneQuery(id).forUpdate()
            LockMode.PESSIMISTIC_READ -> getOneQuery(id).forShare()
        }.fetchOneInto(JTransactions::class.java)
    }

    override fun findWithLockByIdOrRaise(id: UUID, lockMode: LockMode): JTransactions {
        return findWithLockById(id, lockMode)
            ?: throw NoSuchElementException("Not found for id $id")
    }

    private fun getAllQuery(ids: Collection<UUID>) = dslContext.selectFrom(TRANSACTIONS)
        .where(ACCOUNTS.ID.emptyIn(ids))

    override fun findAllWithLockByIds(ids: Collection<UUID>, lockMode: LockMode): List<JTransactions> {
        return when (lockMode) {
            LockMode.PESSIMISTIC_WRITE -> getAllQuery(ids).forUpdate()
            LockMode.PESSIMISTIC_READ -> getAllQuery(ids).forShare()
        }.fetchInto(JTransactions::class.java)
    }

    override fun findAllByIds(ids: Collection<UUID>): List<JTransactions> {
        return getAllQuery(ids).fetchInto(JTransactions::class.java)
    }
}