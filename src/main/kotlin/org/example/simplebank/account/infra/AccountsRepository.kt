package org.example.simplebank.account.infra

import java.util.UUID
import org.example.simplebank.common.dataAccess.BaseCrudRepository
import org.jooq.generated.tables.pojos.JAccountsPojo

interface AccountsRepository :
    BaseCrudRepository<JAccountsPojo, UUID>