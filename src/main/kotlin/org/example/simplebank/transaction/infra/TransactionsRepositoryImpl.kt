package org.example.simplebank.transaction.infra

import java.util.UUID
import org.jooq.generated.tables.pojos.JTransactions
import org.springframework.stereotype.Repository

@Repository
class TransactionsRepositoryImpl(
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
}