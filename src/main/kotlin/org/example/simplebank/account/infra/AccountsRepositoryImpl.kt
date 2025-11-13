package org.example.simplebank.account.infra

import java.util.UUID
import org.example.simplebank.common.dataAccess.LockMode
import org.jooq.DSLContext
import org.jooq.generated.tables.pojos.JAccounts
import org.springframework.stereotype.Repository

@Repository
class AccountsRepositoryImpl(
    private val dslContext: DSLContext,
    private val jooqCrudRepository: AccountsJooqCrudRepository
) : AccountsRepository {
    override fun getById(id: UUID, lockMode: LockMode) = jooqCrudRepository.getById(id)
    override fun getByIdOrRaise(id: UUID, lockMode: LockMode) = jooqCrudRepository.getByIdOrRaise(id)
    override fun getAllByIdsWithLock(ids: Collection<UUID>, lockMode: LockMode): List<JAccounts> =
        jooqCrudRepository.getAllByIdsWithLock(ids, lockMode)

    override fun save(entity: JAccounts) = jooqCrudRepository.save(entity)
    override fun update(entity: JAccounts) = jooqCrudRepository.update(entity)
    override fun delete(entity: JAccounts) = jooqCrudRepository.delete(entity)
    override fun deleteById(id: UUID) = jooqCrudRepository.deleteById(id)
    override fun existsById(id: UUID) = jooqCrudRepository.existsById(id)
    override fun saveAll(entities: Collection<JAccounts>) = jooqCrudRepository.saveAll(entities)
    override fun updateAll(entities: Collection<JAccounts>) = jooqCrudRepository.updateAll(entities)
    override fun deleteAll(entities: Collection<JAccounts>) = jooqCrudRepository.deleteAll(entities)
}