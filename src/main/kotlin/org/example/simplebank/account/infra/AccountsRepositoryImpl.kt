package org.example.simplebank.account.infra

import java.util.UUID
import org.example.simplebank.common.utils.PaginationRequest
import org.example.simplebank.common.utils.PaginationResponse
import org.jooq.generated.tables.pojos.JAccounts
import org.springframework.stereotype.Repository

@Repository
class AccountsRepositoryImpl(
    private val jooqCrudRepository: AccountsJooqCrudRepository
) : AccountsRepository {
    override fun getById(id: UUID) = jooqCrudRepository.getById(id)
    override fun save(entity: JAccounts) = jooqCrudRepository.save(entity)
    override fun update(entity: JAccounts) = jooqCrudRepository.update(entity)
    override fun delete(entity: JAccounts) = jooqCrudRepository.delete(entity)
    override fun deleteById(id: UUID) = jooqCrudRepository.deleteById(id)
    override fun existsById(id: UUID) = jooqCrudRepository.existsById(id)
    override fun saveAll(entities: Collection<JAccounts>) = jooqCrudRepository.saveAll(entities)
    override fun updateAll(entities: Collection<JAccounts>) = jooqCrudRepository.updateAll(entities)
    override fun deleteAll(entities: Collection<JAccounts>) = jooqCrudRepository.deleteAll(entities)

    override fun paginate(pageable: PaginationRequest): PaginationResponse<AccountsPageRes> {
        TODO("Not yet implemented")
    }
}