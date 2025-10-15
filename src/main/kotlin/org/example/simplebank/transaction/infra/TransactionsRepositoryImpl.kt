package org.example.simplebank.transaction.infra

import java.util.UUID
import org.example.simplebank.common.utils.PaginationRequest
import org.example.simplebank.common.utils.PaginationResponse
import org.jooq.generated.tables.pojos.JTransactions
import org.springframework.stereotype.Repository

@Repository
class TransactionsRepositoryImpl(
    private val jooqCrudRepository: TransactionsJooqCrudRepository
) : TransactionsRepository {
    override fun getById(id: UUID) = jooqCrudRepository.getById(id)
    override fun save(entity: JTransactions) = jooqCrudRepository.save(entity)
    override fun update(entity: JTransactions) = jooqCrudRepository.update(entity)
    override fun delete(entity: JTransactions) = jooqCrudRepository.delete(entity)
    override fun deleteById(id: UUID) = jooqCrudRepository.deleteById(id)
    override fun existsById(id: UUID) = jooqCrudRepository.existsById(id)
    override fun saveAll(entities: Collection<JTransactions>) = jooqCrudRepository.saveAll(entities)
    override fun updateAll(entities: Collection<JTransactions>) = jooqCrudRepository.updateAll(entities)
    override fun deleteAll(entities: Collection<JTransactions>) = jooqCrudRepository.deleteAll(entities)

    override fun paginate(pageable: PaginationRequest): PaginationResponse<TransactionsPageRes> {
        TODO("Not yet implemented")
    }
}