package org.example.simplebank.accounts

import org.assertj.core.api.Assertions.assertThat
import org.example.simplebank.account.infra.AccountsJooqCrudRepository
import org.example.simplebank.account.infra.AccountsRepository
import org.example.simplebank.account.infra.AccountsRepositoryImpl
import org.example.simplebank.utils.BaseIntegTest
import org.jooq.generated.tables.pojos.JAccountsPojo
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(
    classes = [
        AccountsRepositoryImpl::class,
        AccountsJooqCrudRepository::class,
    ]
)
class AccountsRepositoryTest : BaseIntegTest() {

    @Autowired
    private lateinit var repository: AccountsRepository

    @Test
    fun `should be able to create, get then delete account`() {
        val account = JAccountsPojo(
            type = "CHECKING",
            accountNumber = "1234567890",
            accountHolderName = "john doe"
        )
        val saved = repository.save(account)
        assertThat(saved.id).isNotNull()

        val id = saved.id!!
        val found = repository.getById(id)
        assertThat(found?.id).isEqualTo(id)

        repository.deleteById(id)
        val shouldBeNull = repository.getById(id)
        assertThat(shouldBeNull).isNull()
    }
}