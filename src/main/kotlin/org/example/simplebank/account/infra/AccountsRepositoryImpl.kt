package org.example.simplebank.account.infra

import java.util.UUID
import org.example.simplebank.common.dataAccess.LockMode
import org.jooq.DSLContext
import org.jooq.generated.tables.pojos.JAccountsPojo
import org.springframework.stereotype.Repository

@Repository
class AccountsRepositoryImpl(
    private val dslContext: DSLContext,
    private val jooqCrudRepository: AccountsJooqCrudRepository
) : AccountsRepository {
    override fun getById(id: UUID, lockMode: LockMode) = jooqCrudRepository.getById(id)
    override fun getByIdOrRaise(id: UUID, lockMode: LockMode) = jooqCrudRepository.getByIdOrRaise(id)
    override fun getAllByIdsWithLock(ids: Collection<UUID>, lockMode: LockMode): List<JAccountsPojo> =
        jooqCrudRepository.getAllByIdsWithLock(ids, lockMode)

    override fun save(entity: JAccountsPojo) = jooqCrudRepository.save(entity)
    override fun update(entity: JAccountsPojo) = jooqCrudRepository.update(entity)
    override fun delete(entity: JAccountsPojo) = jooqCrudRepository.delete(entity)
    override fun deleteById(id: UUID) = jooqCrudRepository.deleteById(id)
    override fun existsById(id: UUID) = jooqCrudRepository.existsById(id)
    override fun saveAll(entities: Collection<JAccountsPojo>) = jooqCrudRepository.saveAll(entities)
    override fun updateAll(entities: Collection<JAccountsPojo>) = jooqCrudRepository.updateAll(entities)
    override fun deleteAll(entities: Collection<JAccountsPojo>) = jooqCrudRepository.deleteAll(entities)
}