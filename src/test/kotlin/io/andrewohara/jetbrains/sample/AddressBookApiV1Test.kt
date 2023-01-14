package io.andrewohara.jetbrains.sample

import io.kotest.assertions.ktor.client.shouldHaveStatus
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
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

    private val book = AddressBook.h2DbExposed(dbInMemory = true, dbName = UUID.randomUUID().toString())

    @Test
    fun `swagger ui - not found`() = book.testClient { client ->
        val resp = client.get("/")
        resp shouldHaveStatus HttpStatusCode.NotFound
    }

    @Test
    fun `get customer - id not uuid`() = book.testClient { client ->
        val resp = client.get("/v1/customers/123")
        resp shouldHaveStatus HttpStatusCode.BadRequest
    }

    @Test
    fun `get customer - empty id`() = book.testClient { client ->
        val resp = client.get("/v1/customers/")
        resp shouldHaveStatus HttpStatusCode.NotFound
    }

    @Test
    fun `get customer - not found`() = book.testClient { client ->
        val resp = client.get("/v1/customers/6a05d083-ca92-454f-aade-0b14ddff4133")
        resp shouldHaveStatus HttpStatusCode.NotFound
    }

    @Test
    fun `get customer - found`() = book.testClient { client ->
        val customer = book.createCustomer(1)

        val resp = client.get("/v1/customers/${customer.id}")
        resp shouldHaveStatus HttpStatusCode.OK
        resp.body<CustomerDtoV1>() shouldBe customer.toDtoV1()
    }

    @Test
    fun `save customer`() = book.testClient { client ->
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
        book.getCustomer(CustomerId.parse(created.id))?.toDtoV1() shouldBe created
    }

    @Test
    fun `save customer - invalid json`() = book.testClient { client ->
        val resp = client.post("/v1/customers") {
            contentType(ContentType.Application.Json)
            setBody("{\"name\":\"foo\"}")
        }

        resp shouldHaveStatus HttpStatusCode.BadRequest
    }

    @Test
    fun `list customers - no filter`() = book.testClient { client ->
        val customer1 = book.createCustomer(1)
        val customer2 = book.createCustomer(2)

        val resp = client.get("/v1/customers")
        resp shouldHaveStatus HttpStatusCode.OK
        resp.body<List<CustomerDtoV1>>().shouldContainExactlyInAnyOrder(
            customer1.toDtoV1(), customer2.toDtoV1()
        )
    }

    @Test
    fun `list customers - name filter`() = book.testClient { client ->
        val customer1 = book.createCustomer(1, name = "name1", email="email")
        book.createCustomer(2, name = "name2", email = "email")

        val resp = client.get("/v1/customers") {
            url {
                parameters.append("name", customer1.name.value)
            }
        }
        resp shouldHaveStatus HttpStatusCode.OK
        resp.body<List<CustomerDtoV1>>().shouldContainExactlyInAnyOrder(
            customer1.toDtoV1()
        )
    }

    @Test
    fun `list customers - email filter`() = book.testClient { client ->
        book.createCustomer(1, name = "name", email = "email1")
        val customer2 = book.createCustomer(2, name = "name", email = "email2")

        val resp = client.get("/v1/customers") {
            url {
                parameters.append("email", customer2.email.value)
            }
        }
        resp shouldHaveStatus HttpStatusCode.OK
        resp.body<List<CustomerDtoV1>>().shouldContainExactlyInAnyOrder(
            customer2.toDtoV1()
        )
    }

    @Test
    fun `list customers - email and name filter`() = book.testClient { client ->
        book.createCustomer(1, name = "name1", email = "email1")
        val customer2 = book.createCustomer(2, name = "name1", email = "email2")
        book.createCustomer(3, name = "name2", email = "email2")

        val resp = client.get("/v1/customers") {
            url {
                parameters.append("name", customer2.name.value)
                parameters.append("email", customer2.email.value)
            }
        }
        resp shouldHaveStatus HttpStatusCode.OK
        resp.body<List<CustomerDtoV1>>().shouldContainExactlyInAnyOrder(
            customer2.toDtoV1()
        )
    }

    @Test
    fun `list customers - empty name and email`() = book.testClient { client ->
        book.createCustomer(1, name = "name1", email = "email1")
        book.createCustomer(2, name = "nane1", email = "email2")
        book.createCustomer(3, name = "name2", email = "email2")

        val resp = client.get("/v1/customers") {
            url {
                parameters.append("name", "")
                parameters.append("email", "")
            }
        }
        resp shouldHaveStatus HttpStatusCode.BadRequest
    }
}