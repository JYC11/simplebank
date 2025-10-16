package org.example.simplebank

import org.jooq.DSLContext
import org.springframework.boot.context.annotation.Configurations
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test")
@Import(
    DSLContext::class,
    Configurations::class,
    TestcontainersConfiguration::class,
)
abstract class BaseIntegTest {
}