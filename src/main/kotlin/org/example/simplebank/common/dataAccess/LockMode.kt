package org.example.simplebank.common.dataAccess

enum class LockMode {
    NONE,
    PESSIMISTIC_WRITE,
    PESSIMISTIC_READ,
}