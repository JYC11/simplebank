package org.example.simplebank.common.utils

data class PaginationResponse<T>(
    val items: List<T>,
    val page: Int,
    val size: Int,
    val total: Long,
)