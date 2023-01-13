package io.andrewohara.jetbrains.sample

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication

class TestDriver {

    operator fun invoke(testFn: suspend (HttpClient) -> Unit) = testApplication {
        application {
            installAddressBook()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }
        testFn(client)
    }
}