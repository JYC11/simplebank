package org.example.simplebank.journalEntries.infra

import java.util.UUID
import org.example.simplebank.common.dataAccess.AbstractJooqCrudRepository
import org.jooq.Configuration as JooqConfiguration
import org.jooq.DSLContext
import org.jooq.generated.tables.daos.JJournalEntriesDao
import org.jooq.generated.tables.pojos.JJournalEntries
import org.jooq.generated.tables.records.JJournalEntriesRecord
import org.springframework.stereotype.Repository

@Repository
class JournalEntriesJooqCrudRepository(
    override val dslContext: DSLContext,
) : AbstractJooqCrudRepository<JJournalEntriesDao, JJournalEntriesRecord, JJournalEntries, UUID>(
    dslContext = dslContext,
    dao = JJournalEntriesDao(dslContext.configuration())
)
