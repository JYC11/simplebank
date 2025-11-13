package org.example.simplebank.account.infra

import java.util.UUID
import org.example.simplebank.common.dataAccess.BaseCrudRepository
import org.jooq.generated.tables.pojos.JAccounts

interface AccountsRepository :
    BaseCrudRepository<JAccounts, UUID>