package io.andrewohara.jetbrains.sample

import io.ktor.server.cio.CIO
import io.ktor.server.engine.embeddedServer
import org.slf4j.LoggerFactory

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080
    val dbInMemory = System.getenv("DB_IN_MEMORY")?.toBoolean() ?: true
    val dbName = System.getenv("DB_NAME") ?: "./addressBook"

    val log = LoggerFactory.getLogger("main")
    log.info("PORT: $port")
    log.info("DB_IN_MEMORY: $dbInMemory")
    log.info("DB_NAME: $dbName")

    val book = AddressBook.h2DbExposed(dbInMemory = dbInMemory, dbName = dbName)

    embeddedServer(CIO, port = port) {
        installAddressBook(book)
    }.start(wait = true)
}