package org.example.simplebank

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class SimplebankApplicationTests {

    @Test
    fun `context loads - smoke test`() {
        assertThat(true).isTrue()
    }

}
