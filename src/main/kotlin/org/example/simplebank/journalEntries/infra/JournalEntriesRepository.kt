package org.example.simplebank.journalEntries.infra

import java.util.UUID
import org.example.simplebank.common.dataAccess.BaseCrudRepository
import org.example.simplebank.common.dataAccess.FindAllRepository
import org.example.simplebank.common.dataAccess.FindWithLockRepository
import org.jooq.generated.tables.pojos.JJournalEntries

interface JournalEntriesRepository :
    BaseCrudRepository<JJournalEntries, UUID>,
    FindWithLockRepository<JJournalEntries, UUID>,
    FindAllRepository<JJournalEntries, UUID>