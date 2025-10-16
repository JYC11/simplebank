package org.example.simplebank.transaction.infra

import java.util.UUID
import org.example.simplebank.common.dataAccess.BaseCrudRepository
import org.example.simplebank.common.dataAccess.FindAllRepository
import org.example.simplebank.common.dataAccess.FindWithLockRepository
import org.jooq.generated.tables.pojos.JTransactions

interface TransactionsRepository :
    BaseCrudRepository<JTransactions, UUID>,
    FindWithLockRepository<JTransactions, UUID>,
    FindAllRepository<JTransactions, UUID>