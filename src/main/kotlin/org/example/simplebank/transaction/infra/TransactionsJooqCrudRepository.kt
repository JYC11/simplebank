package org.example.simplebank.transaction.infra

import java.util.UUID
import org.example.simplebank.common.dataAccess.AbstractJooqCrudRepository
import org.jooq.DSLContext
import org.jooq.generated.tables.daos.JTransactionsDao
import org.jooq.generated.tables.pojos.JTransactions
import org.jooq.generated.tables.records.JTransactionsRecord
import org.jooq.generated.tables.references.TRANSACTIONS
import org.springframework.stereotype.Repository
import org.jooq.generated.tables.JTransactions as JTransactionsTable

@Repository
class TransactionsJooqCrudRepository(
    override val dslContext: DSLContext,
) : AbstractJooqCrudRepository<
        JTransactionsDao,
        JTransactionsRecord,
        JTransactionsTable,
        JTransactions,
        UUID
        >(
    dslContext = dslContext,
    table = TRANSACTIONS,
    idField = TRANSACTIONS.ID,
    dao = JTransactionsDao(dslContext.configuration())
)
