package io.andrewohara.jetbrains.sample

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication

fun AddressBook.testClient(testFn: suspend (HttpClient) -> Unit) = testApplication {
    application {
        installAddressBook(this@testClient)
    }

    val client = createClient {
        install(ContentNegotiation) {
            json()
        }
    }

    testFn(client)
}

fun AddressBook.createCustomer(
    number: Int,
    vararg addresses: Address,
    name: String = "customer $number",
    email: String = "customer$number@fakemail.xyz"
) = CustomerData(
    name = CustomerName.of(name),
    email = EmailAddress.of(email),
    addresses = addresses.toList()
).let(saveCustomer)

fun address(number: Int) = Address(
    streetName = "$number my street",
    city = "city $number",
    postCode = "postcode $number",
    state = "state $number",
    country = "country $number"
)