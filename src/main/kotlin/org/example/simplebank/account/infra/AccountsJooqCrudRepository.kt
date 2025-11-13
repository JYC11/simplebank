package org.example.simplebank.account.infra

import java.util.UUID
import org.example.simplebank.common.dataAccess.AbstractJooqCrudRepository
import org.jooq.DSLContext
import org.jooq.generated.tables.JAccountsTable
import org.jooq.generated.tables.daos.JAccountsDao
import org.jooq.generated.tables.pojos.JAccountsPojo
import org.jooq.generated.tables.records.JAccountsRecord
import org.jooq.generated.tables.references.ACCOUNTS
import org.springframework.stereotype.Repository

@Repository
class AccountsJooqCrudRepository(
    override val dslContext: DSLContext,
) : AbstractJooqCrudRepository<
        JAccountsDao,
        JAccountsRecord,
        JAccountsTable,
        JAccountsPojo,
        UUID,
        >(
    dslContext = dslContext,
    table = ACCOUNTS,
    idField = ACCOUNTS.ID,
    dao = JAccountsDao(dslContext.configuration())
)
