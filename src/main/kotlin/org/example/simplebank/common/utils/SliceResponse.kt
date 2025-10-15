package org.example.simplebank.common.utils

data class SliceResponse<T>(
    val items: List<T>,
    val page: Int,
    val size: Int,
    val hasNext: Boolean,
)