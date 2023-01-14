package io.andrewohara.jetbrains.sample

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    val dbInMemory = System.getenv("DB_IN_MEMORY")?.toBoolean() ?: true
    val dbName = System.getenv("DB_NAME") ?: "./addressBook"

    val book = AddressBook.h2DbExposed(dbInMemory = dbInMemory, dbName = dbName)

    embeddedServer(Netty, port = port) {
        installAddressBook(book)
    }.start(wait = true)
}