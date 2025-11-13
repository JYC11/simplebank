package org.example.simplebank.transaction.infra

import java.util.UUID
import org.example.simplebank.common.dataAccess.AbstractJooqCrudRepository
import org.jooq.DSLContext
import org.jooq.generated.tables.JTransactionsTable
import org.jooq.generated.tables.daos.JTransactionsDao
import org.jooq.generated.tables.pojos.JTransactionsPojo
import org.jooq.generated.tables.records.JTransactionsRecord
import org.jooq.generated.tables.references.TRANSACTIONS
import org.springframework.stereotype.Repository

@Repository
class TransactionsJooqCrudRepository(
    override val dslContext: DSLContext,
) : AbstractJooqCrudRepository<
        JTransactionsDao,
        JTransactionsRecord,
        JTransactionsTable,
        JTransactionsPojo,
        UUID
        >(
    dslContext = dslContext,
    table = TRANSACTIONS,
    idField = TRANSACTIONS.ID,
    dao = JTransactionsDao(dslContext.configuration())
)
