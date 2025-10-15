package org.example.simplebank.common.config

import javax.sql.DataSource
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.conf.ExecuteWithoutWhere
import org.jooq.impl.DefaultDSLContext
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JooqConfig {
    @Bean
    fun dslContext(primaryDataSource: DataSource): DSLContext {
        return DefaultDSLContext(primaryDataSource, SQLDialect.POSTGRES)
    }

    @Bean
    fun jooqDefaultConfigurationCustomizer(): DefaultConfigurationCustomizer {
        return DefaultConfigurationCustomizer { configuration ->
            configuration.settings()
                .withExecuteDeleteWithoutWhere(ExecuteWithoutWhere.THROW)
                .withExecuteUpdateWithoutWhere(ExecuteWithoutWhere.THROW)
                .withRenderSchema(false)
        }
    }
}