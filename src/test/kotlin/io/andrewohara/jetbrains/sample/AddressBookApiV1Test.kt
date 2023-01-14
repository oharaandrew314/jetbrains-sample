package io.andrewohara.jetbrains.sample

import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.matchers.shouldBe
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import org.junit.jupiter.api.Test
import java.util.UUID

class AddressBookApiV1Test {

    private val app = AddressBookApplication(dbInMemory = true, dbName = UUID.randomUUID().toString())

    @Test
    fun `swagger ui - not found`() = app.testClient { client ->
        val resp = client.get("/")
        resp shouldHaveStatus HttpStatusCode.NotFound
    }

    @Test
    fun `get customer - malformed id`() = app.testClient { client ->
        val resp = client.get("/v1/customers/123")
        resp shouldHaveStatus HttpStatusCode.BadRequest
    }

    @Test
    fun `get customer - not found`() = app.testClient { client ->
        val resp = client.get("/v1/customers/6a05d083-ca92-454f-aade-0b14ddff4133")
        resp shouldHaveStatus HttpStatusCode.NotFound
    }

    @Test
    fun `get customer - found`() = app.testClient { client ->
        val data = customerData(1, address(2), address(3))

        val customer = saveCustomer(data)

        val resp = client.get("/v1/customers/${customer.id}")
        resp shouldHaveStatus HttpStatusCode.OK
        resp.body<CustomerDtoV1>() shouldBe customer.toDtoV1()
    }

    @Test
    fun `save customer`() = app.testClient { client ->
        val data = CustomerDataDtoV1(
            name = "John Doe",
            email = "johndoe@fakemail.xyz",
            addresses = setOf(address(1).toDtoV1(), address(2).toDtoV1())
        )

        val resp = client.post("/v1/customers") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }

        // verify response body
        resp shouldHaveStatus HttpStatusCode.Created
        val created = resp.body<CustomerDtoV1>()
        created shouldBe CustomerDtoV1(
            id = created.id,
            name = data.name,
            email = data.email,
            addresses = data.addresses
        )

        // verify saved resource
        getCustomer(CustomerId.parse(created.id))?.toDtoV1() shouldBe created
    }
}