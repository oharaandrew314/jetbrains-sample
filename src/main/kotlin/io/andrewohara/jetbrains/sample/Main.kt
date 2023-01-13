package io.andrewohara.jetbrains.sample

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main() {
    val port = System.getenv("PORT")?.toInt() ?: 8080

    embeddedServer(Netty, port = port) {
        installAddressBook()
    }.start(wait = true)
}