package io.andrewohara.jetbrains.sample

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication

fun AddressBook.testClient(testFn: suspend AddressBook.(HttpClient) -> Unit) = testApplication {
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

fun customerData(number: Int, vararg addresses: Address) = CustomerData(
    name = CustomerName.of("customer $number"),
    email = EmailAddress.of("customer$number@fakemail.xyz"),
    addresses = addresses.toList()
)

fun address(number: Int) = Address(
    streetName = "$number my street",
    city = "city $number",
    postCode = "postcode $number",
    state = "state $number",
    country = "country $number"
)