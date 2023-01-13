package io.andrewohara.jetbrains.sample

import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import org.junit.jupiter.api.Test

class AddressBookContract {

    private val driver = TestDriver()

    @Test
    fun `missing route 404`() = driver { client ->
        val resp = client.get("/")
        resp shouldHaveStatus HttpStatusCode.NotFound
    }
}