package io.andrewohara.jetbrains.sample

import dev.forkhandles.values.parseOrNull
import io.andrewohara.jetbrains.sample.actions.GetCustomer
import io.andrewohara.jetbrains.sample.actions.SaveCustomer
import io.andrewohara.jetbrains.sample.actions.exposed
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class AddressBookApplication private constructor(
    val getCustomer: GetCustomer,
    val saveCustomer: SaveCustomer
) {
    companion object {
        operator fun invoke(database: Database) = AddressBookApplication(
            getCustomer = GetCustomer.exposed(database),
            saveCustomer = SaveCustomer.exposed(database)
        )

        operator fun invoke(dbInMemory: Boolean, dbName: String): AddressBookApplication {
            val database = if (dbInMemory) {
                Database.connect("jdbc:h2:mem:$dbName;DB_CLOSE_DELAY=-1")
            } else {
                Database.connect("jdbc:h2:file:$dbName")
            }

            transaction(database) {
                SchemaUtils.createMissingTablesAndColumns(CustomerTable, AddressTable)
            }

            return this(database)
        }
    }
}

fun Application.installAddressBook(app: AddressBookApplication) {
    install(ContentNegotiation) {
        json()
    }
    routing {
        get("/v1/customers/{id}") {
            val id = call.parameters["id"]
                ?.let(CustomerId::parseOrNull)
                ?: return@get call.respond(HttpStatusCode.BadRequest)

            val customer = app.getCustomer(id)
                ?: return@get call.respond(HttpStatusCode.NotFound)

            call.respond(customer.toDtoV1())
        }
        post("/v1/customers") {
            val customer = call
                .receive<CustomerDataDtoV1>().toInternal()
                .let(app.saveCustomer)

            call.respond(HttpStatusCode.Created, customer.toDtoV1())
        }
    }
}