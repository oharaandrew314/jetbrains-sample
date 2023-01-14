package io.andrewohara.jetbrains.sample.actions

import io.andrewohara.jetbrains.sample.AddressTable
import io.andrewohara.jetbrains.sample.Customer
import io.andrewohara.jetbrains.sample.CustomerData
import io.andrewohara.jetbrains.sample.CustomerDataDtoV1
import io.andrewohara.jetbrains.sample.CustomerId
import io.andrewohara.jetbrains.sample.CustomerTable
import io.andrewohara.jetbrains.sample.toDtoV1
import io.andrewohara.jetbrains.sample.toInternal
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.post
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction

fun interface SaveCustomer: (CustomerData) -> Customer {
    companion object
}

fun SaveCustomer.Companion.exposed(database: Database) = SaveCustomer { data ->
    transaction(database) {
        val id = CustomerTable.insertAndGetId { row ->
            row[name] = data.name.value
            row[email] = data.email.value
        }

        for (address in data.addresses) {
            AddressTable.insert { row ->
                row[customerId] = id
                row[streetName] = address.streetName
                row[city] = address.city
                row[postCode] = address.postCode
                row[state] = address.state
                row[country] = address.country
            }
        }

        Customer(
            id = CustomerId.of(id.value),
            name = data.name,
            email = data.email,
            addresses = data.addresses
        )
    }
}

fun SaveCustomer.toRouteV1(routing: Routing) = routing.post("/v1/customers") {
    val customer = call
        .receive<CustomerDataDtoV1>().toInternal()
        .let(this@toRouteV1)

    call.respond(HttpStatusCode.Created, customer.toDtoV1())
}