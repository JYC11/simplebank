package org.example.simplebank.account.infra

import java.util.UUID
import org.example.simplebank.common.dataAccess.LockMode
import org.example.simplebank.common.dataAccess.emptyIn
import org.jooq.DSLContext
import org.jooq.generated.tables.pojos.JAccounts
import org.jooq.generated.tables.references.ACCOUNTS
import org.springframework.stereotype.Repository

@Repository
class AccountsRepositoryImpl(
    private val dslContext: DSLContext,
    private val jooqCrudRepository: AccountsJooqCrudRepository
) : AccountsRepository {
    override fun getById(id: UUID) = jooqCrudRepository.getById(id)
    override fun getByIdOrRaise(id: UUID) = jooqCrudRepository.getByIdOrRaise(id)
    override fun save(entity: JAccounts) = jooqCrudRepository.save(entity)
    override fun update(entity: JAccounts) = jooqCrudRepository.update(entity)
    override fun delete(entity: JAccounts) = jooqCrudRepository.delete(entity)
    override fun deleteById(id: UUID) = jooqCrudRepository.deleteById(id)
    override fun existsById(id: UUID) = jooqCrudRepository.existsById(id)
    override fun saveAll(entities: Collection<JAccounts>) = jooqCrudRepository.saveAll(entities)
    override fun updateAll(entities: Collection<JAccounts>) = jooqCrudRepository.updateAll(entities)
    override fun deleteAll(entities: Collection<JAccounts>) = jooqCrudRepository.deleteAll(entities)

    private fun getOneQuery(id: UUID) = dslContext.selectFrom(ACCOUNTS)
        .where(ACCOUNTS.ID.eq(id))

    override fun findWithLockById(id: UUID, lockMode: LockMode): JAccounts? {
        return when (lockMode) {
            LockMode.PESSIMISTIC_WRITE -> getOneQuery(id).forUpdate()
            LockMode.PESSIMISTIC_READ -> getOneQuery(id).forShare()
        }.fetchOneInto(JAccounts::class.java)
    }

    override fun findWithLockByIdOrRaise(id: UUID, lockMode: LockMode): JAccounts {
        return findWithLockById(id, lockMode)
            ?: throw NoSuchElementException("Not found for id $id")
    }

    private fun getAllQuery(ids: Collection<UUID>) = dslContext.selectFrom(ACCOUNTS)
        .where(ACCOUNTS.ID.emptyIn(ids))

    override fun findAllWithLockByIds(ids: Collection<UUID>, lockMode: LockMode): List<JAccounts> {
        return when (lockMode) {
            LockMode.PESSIMISTIC_WRITE -> getAllQuery(ids).forUpdate()
            LockMode.PESSIMISTIC_READ -> getAllQuery(ids).forShare()
        }.fetchInto(JAccounts::class.java)
    }

    override fun findAllByIds(ids: Collection<UUID>): List<JAccounts> {
        return getAllQuery(ids).fetchInto(JAccounts::class.java)
    }
}