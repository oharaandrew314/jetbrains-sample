package io.andrewohara.jetbrains.sample

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.nio.file.Files
import java.util.UUID

abstract class AddressBookContract(private val book: AddressBook) {

    @Test
    fun `get customer - not found`() {
        val id = CustomerId.parse("6a05d083-ca92-454f-aade-0b14ddff4133")
        book.getCustomer(id).shouldBeNull()
    }

    @Test
    fun `save and get customer - found`() {
        val customer = customerData(5, address(6), address(7), address(8))
            .let(book.saveCustomer)

        book.getCustomer(customer.id) shouldBe customer
    }
}

class InMemoryAddressBookTest: AddressBookContract(
    AddressBook.h2DbExposed(dbInMemory = true, dbName = UUID.randomUUID().toString())
)

class TempFileAddressBookTest: AddressBookContract(
    Files.createTempFile("tempdb", "db")
        .also { it.toFile().deleteOnExit() }
        .let { AddressBook.h2DbExposed(dbInMemory = false, dbName = it.toString()) }
)
