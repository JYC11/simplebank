package org.example.simplebank.journalEntries.infra

import java.util.UUID
import org.example.simplebank.common.dataAccess.LockMode
import org.example.simplebank.common.dataAccess.emptyIn
import org.jooq.DSLContext
import org.jooq.generated.tables.pojos.JJournalEntries
import org.jooq.generated.tables.references.JOURNAL_ENTRIES
import org.springframework.stereotype.Repository

@Repository
class JournalEntriesRepositoryImpl(
    private val dslContext: DSLContext,
    private val jooqCrudRepository: JournalEntriesJooqCrudRepository
) : JournalEntriesRepository {
    override fun getById(id: UUID) = jooqCrudRepository.getById(id)
    override fun getByIdOrRaise(id: UUID) = jooqCrudRepository.getByIdOrRaise(id)
    override fun save(entity: JJournalEntries) = jooqCrudRepository.save(entity)
    override fun update(entity: JJournalEntries) = throw UnsupportedOperationException()
    override fun delete(entity: JJournalEntries) = throw UnsupportedOperationException()
    override fun deleteById(id: UUID) = throw UnsupportedOperationException()
    override fun existsById(id: UUID) = jooqCrudRepository.existsById(id)
    override fun saveAll(entities: Collection<JJournalEntries>) = jooqCrudRepository.saveAll(entities)
    override fun updateAll(entities: Collection<JJournalEntries>) = throw UnsupportedOperationException()
    override fun deleteAll(entities: Collection<JJournalEntries>) = throw UnsupportedOperationException()

    private fun getOneQuery(id: UUID) = dslContext.selectFrom(JOURNAL_ENTRIES)
        .where(JOURNAL_ENTRIES.ID.eq(id))

    override fun findWithLockById(id: UUID, lockMode: LockMode): JJournalEntries? {
        return when (lockMode) {
            LockMode.PESSIMISTIC_WRITE -> getOneQuery(id).forUpdate()
            LockMode.PESSIMISTIC_READ -> getOneQuery(id).forShare()
        }.fetchOneInto(JJournalEntries::class.java)
    }

    override fun findWithLockByIdOrRaise(id: UUID, lockMode: LockMode): JJournalEntries {
        return findWithLockById(id, lockMode)
            ?: throw NoSuchElementException("Not found for id $id")
    }

    private fun getAllQuery(ids: Collection<UUID>) = dslContext.selectFrom(JOURNAL_ENTRIES)
        .where(JOURNAL_ENTRIES.ID.emptyIn(ids))

    override fun findAllWithLockByIds(ids: Collection<UUID>, lockMode: LockMode): List<JJournalEntries> {
        return when (lockMode) {
            LockMode.PESSIMISTIC_WRITE -> getAllQuery(ids).forUpdate()
            LockMode.PESSIMISTIC_READ -> getAllQuery(ids).forShare()
        }.fetchInto(JJournalEntries::class.java)
    }

    override fun findAllByIds(ids: Collection<UUID>): List<JJournalEntries> {
        return getAllQuery(ids).fetchInto(JJournalEntries::class.java)
    }
}