package io.andrewohara.jetbrains.sample.actions

import dev.forkhandles.values.parseOrNull
import io.andrewohara.jetbrains.sample.Address
import io.andrewohara.jetbrains.sample.AddressTable
import io.andrewohara.jetbrains.sample.Customer
import io.andrewohara.jetbrains.sample.CustomerId
import io.andrewohara.jetbrains.sample.CustomerName
import io.andrewohara.jetbrains.sample.CustomerTable
import io.andrewohara.jetbrains.sample.EmailAddress
import io.andrewohara.jetbrains.sample.toDtoV1
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.get
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.lowerCase
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun interface ListCustomers: (CustomerName?, EmailAddress?) -> Collection<Customer> {
    operator fun invoke() = invoke(null, null)
    operator fun invoke(customerName: CustomerName) = invoke(customerName, null)
    operator fun invoke(emailAddress: EmailAddress) = invoke(null, emailAddress)

    companion object
}

fun ListCustomers.Companion.exposed(database: Database) = ListCustomers { name, email ->
    transaction(database) {
        val customers = CustomerTable
            .select {
                listOfNotNull(
                    name?.let { CustomerTable.name.lowerCase() eq it.value.lowercase() },
                    email?.let { CustomerTable.email.lowerCase() eq it.value.lowercase() }
                ).takeIf { it.isNotEmpty() }
                    ?.reduce { op1, op2 -> op1 and op2 }
                    ?: Op.TRUE
            }
            .toList()

        val customerIds = customers.map { it[CustomerTable.id] }

        val addresses = AddressTable
            .select { AddressTable.customerId inList customerIds }
            .groupBy(
                keySelector = { it[AddressTable.customerId] },
                valueTransform = { row ->
                    Address(
                        streetName = row[AddressTable.streetName],
                        city = row[AddressTable.city],
                        postCode = row[AddressTable.postCode],
                        state = row[AddressTable.state],
                        country = row[AddressTable.country]
                    )
                }
            )

        customers.map { row ->
            val id = row[CustomerTable.id]
            Customer(
                id = CustomerId.of(id.value),
                name = CustomerName.of(row[CustomerTable.name]),
                email = EmailAddress.of(row[CustomerTable.email]),
                addresses = addresses[id].orEmpty()
            )
        }
    }
}

fun ListCustomers.toRouteV1(routing: Routing) = routing.get("/v1/customers") {
    val name = call.request.queryParameters["name"]?.let {
        CustomerName.parseOrNull(it) ?: return@get call.respond(HttpStatusCode.BadRequest)
    }
    val email = call.request.queryParameters["email"]?.let {
        EmailAddress.parseOrNull(it) ?: return@get call.respond(HttpStatusCode.BadRequest)
    }

    invoke(name, email)
        .map { it.toDtoV1() }
        .let { call.respond(it) }
}