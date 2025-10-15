package org.example.simplebank.account.infra

import java.util.UUID
import org.example.simplebank.common.dataAccess.BaseCrudRepository
import org.example.simplebank.common.utils.PaginationRequest
import org.example.simplebank.common.utils.PaginationResponse
import org.jooq.generated.tables.pojos.JAccounts

interface AccountsRepository : BaseCrudRepository<JAccounts, UUID> {
    fun paginate(pageable: PaginationRequest): PaginationResponse<AccountsPageRes>
}