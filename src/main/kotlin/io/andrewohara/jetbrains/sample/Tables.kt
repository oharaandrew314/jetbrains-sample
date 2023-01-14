package io.andrewohara.jetbrains.sample

import org.jetbrains.exposed.dao.id.UUIDTable

object CustomerTable: UUIDTable("customer") {
    val name = varchar("name", 256).index()
    val email = varchar("email", 512).index()
}

object AddressTable: UUIDTable("addresses") {
    val customerId = reference("customer_id", CustomerTable).index()
    val streetName = varchar("street_name", 256)
    val city = varchar("city", 128)
    val postCode = varchar("post_code", 128)
    val state = varchar("state", 128)
    val country = varchar("country", 128)
}
