package org.example.simplebank.transaction.infra

import java.util.UUID
import org.example.simplebank.common.dataAccess.BaseCrudRepository
import org.jooq.generated.tables.pojos.JTransactions

interface TransactionsRepository : BaseCrudRepository<JTransactions, UUID>