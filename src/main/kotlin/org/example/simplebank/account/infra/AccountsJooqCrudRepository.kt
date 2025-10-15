package org.example.simplebank.account.infra

import java.util.UUID
import org.example.simplebank.common.dataAccess.AbstractJooqCrudRepository
import org.jooq.Configuration
import org.jooq.DSLContext
import org.jooq.generated.tables.daos.JAccountsDao
import org.jooq.generated.tables.pojos.JAccounts
import org.jooq.generated.tables.records.JAccountsRecord
import org.springframework.stereotype.Repository

@Repository
class AccountsJooqCrudRepository(
    override val dslContext: DSLContext,
    override val configuration: Configuration,
) : AbstractJooqCrudRepository<JAccountsDao, JAccountsRecord, JAccounts, UUID>(
    dslContext = dslContext,
    configuration = configuration,
    dao = JAccountsDao(configuration)
)
