package io.andrewohara.jetbrains.sample

import io.andrewohara.jetbrains.sample.actions.GetCustomer
import io.andrewohara.jetbrains.sample.actions.ListCustomers
import io.andrewohara.jetbrains.sample.actions.SaveCustomer
import io.andrewohara.jetbrains.sample.actions.exposed
import io.andrewohara.jetbrains.sample.actions.toRouteV1
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

class AddressBook(
    val getCustomer: GetCustomer,
    val saveCustomer: SaveCustomer,
    val listCustomers: ListCustomers
) {
    companion object
}

fun AddressBook.Companion.h2DbExposed(dbInMemory: Boolean, dbName: String): AddressBook {
    val database = if (dbInMemory) {
        Database.connect("jdbc:h2:mem:$dbName;DB_CLOSE_DELAY=-1")
    } else {
        Database.connect("jdbc:h2:file:$dbName")
    }

    transaction(database) {
        SchemaUtils.createMissingTablesAndColumns(CustomerTable, AddressTable)
    }

    return AddressBook(
        getCustomer = GetCustomer.exposed(database),
        saveCustomer = SaveCustomer.exposed(database),
        listCustomers = ListCustomers.exposed(database)
    )
}

fun Application.installAddressBook(book: AddressBook) {
    install(ContentNegotiation) {
        json()
    }
    routing {
        book.saveCustomer.toRouteV1(this)
        book.getCustomer.toRouteV1(this)
        book.listCustomers.toRouteV1(this)
    }
}
