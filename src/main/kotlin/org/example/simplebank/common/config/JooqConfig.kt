package org.example.simplebank.common.config

import javax.sql.DataSource
import org.jooq.DSLContext
import org.jooq.SQLDialect
import org.jooq.conf.ExecuteWithoutWhere
import org.jooq.Configuration as JooqConfiguration
import org.jooq.impl.DefaultDSLContext
import org.springframework.boot.autoconfigure.jooq.DefaultConfigurationCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JooqConfig {
    @Bean
    fun dslContext(dataSource: DataSource): DSLContext {
        return DefaultDSLContext(dataSource, SQLDialect.POSTGRES)
    }

    @Bean
    fun jooqConfiguration(dslContext: DSLContext): JooqConfiguration {
        return dslContext.configuration()
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