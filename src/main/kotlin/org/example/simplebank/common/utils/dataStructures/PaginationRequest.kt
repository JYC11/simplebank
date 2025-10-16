package org.example.simplebank.common.utils.dataStructures

data class PaginationRequest(
    val page: Int = 1,
    val size: Int = 20,
    val sort: String? = null,
    val paged: Boolean = true,
) {
    init {
        require(page > 0) { "Page must be greater than 0" }
        require(size > 0 && size < 200) { "Size must be greater than 0" }
    }
}