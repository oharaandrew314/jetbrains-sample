package io.andrewohara.jetbrains.sample

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication

fun AddressBookApplication.testClient(testFn: suspend AddressBookApplication.(HttpClient) -> Unit) = testApplication {
    application {
        installAddressBook(this@testClient)
    }

    val client = createClient {
        install(ContentNegotiation) {
            json()
        }
    }
    testFn(this@testClient, client)
}