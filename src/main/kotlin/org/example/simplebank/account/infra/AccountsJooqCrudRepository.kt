package org.example.simplebank.account.infra

import java.util.UUID
import org.example.simplebank.common.dataAccess.AbstractJooqCrudRepository
import org.jooq.DSLContext
import org.jooq.generated.tables.daos.JAccountsDao
import org.jooq.generated.tables.pojos.JAccounts
import org.jooq.generated.tables.records.JAccountsRecord
import org.jooq.generated.tables.references.ACCOUNTS
import org.springframework.stereotype.Repository
import org.jooq.generated.tables.JAccounts as JAccountsTable

@Repository
class AccountsJooqCrudRepository(
    override val dslContext: DSLContext,
) : AbstractJooqCrudRepository<
        JAccountsDao,
        JAccountsRecord,
        JAccountsTable,
        JAccounts,
        UUID,
        >(
    dslContext = dslContext,
    table = ACCOUNTS,
    idField = ACCOUNTS.ID,
    dao = JAccountsDao(dslContext.configuration())
)
