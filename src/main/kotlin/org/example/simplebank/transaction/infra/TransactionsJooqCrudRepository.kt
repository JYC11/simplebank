package org.example.simplebank.transaction.infra

import java.util.UUID
import org.example.simplebank.common.dataAccess.AbstractJooqCrudRepository
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.generated.tables.daos.JTransactionsDao
import org.jooq.generated.tables.pojos.JTransactions
import org.jooq.generated.tables.records.JTransactionsRecord
import org.springframework.stereotype.Repository

@Repository
class TransactionsJooqCrudRepository(
    override val dslContext: DSLContext,
    override val configuration: Configuration,
) : AbstractJooqCrudRepository<JTransactionsDao, JTransactionsRecord, JTransactions, UUID>(
    dslContext = dslContext,
    configuration = configuration,
    dao = JTransactionsDao(configuration)
)
