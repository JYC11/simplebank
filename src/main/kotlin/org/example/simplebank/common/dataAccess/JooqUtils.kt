package org.example.simplebank.common.dataAccess

import org.jooq.Condition
import org.jooq.Field
import org.jooq.impl.DSL

fun <T> Field<T?>.emptyIn(comparators: Collection<T>): Condition {
    if (comparators.isEmpty()) {
        return DSL.falseCondition()
    }
    return this.`in`(comparators)
}

fun Field<String?>.containsIfNotBlank(comparator: String?): Condition {
    if (comparator.isNullOrBlank()) {
        return DSL.noCondition()
    }
    return this.likeIgnoreCase("%$comparator%")
}