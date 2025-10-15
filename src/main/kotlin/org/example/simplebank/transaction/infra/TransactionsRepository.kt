package org.example.simplebank.transaction.infra

import java.util.UUID
import org.example.simplebank.common.dataAccess.BaseCrudRepository
import org.example.simplebank.common.utils.PaginationRequest
import org.example.simplebank.common.utils.PaginationResponse
import org.jooq.generated.tables.pojos.JTransactions

interface TransactionsRepository : BaseCrudRepository<JTransactions, UUID> {
    fun paginate(pageable: PaginationRequest): PaginationResponse<TransactionsPageRes>
}