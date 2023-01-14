package io.andrewohara.jetbrains.sample

import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
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
        val customer1 = book.createCustomer(5, address(6), address(7), address(8))
        val customer2 = book.createCustomer(9, address(10))

        book.getCustomer(customer1.id) shouldBe customer1
        book.getCustomer(customer2.id) shouldBe customer2
    }

    @Test
    fun `list customers - no filter`() {
        val customer1 = book.createCustomer(5, address(6), address(7), address(8))
        val customer2 = book.createCustomer(9, address(10))

        book.listCustomers().shouldContainExactlyInAnyOrder(customer1, customer2)
    }

    @Test
    fun `list customers - by exact name`() {
        val customer1 = book.createCustomer(1, name = "Paul Atreides")
        val customer2 = book.createCustomer(2, name = "Paul Atreides")
        book.createCustomer(3, name = "Paul Smith")

        book.listCustomers(customer1.name).shouldContainExactlyInAnyOrder(customer1, customer2)
    }

    @Test
    fun `list customers - by partial name (not supported)`() {
        book.createCustomer(1, name = "Paul Atreides")
        book.createCustomer(2, name = "Paul Smith")
        book.createCustomer(3)

        book.listCustomers(CustomerName.of("Paul")).shouldBeEmpty()
    }

    @Test
    fun `list customers - by case-insensitive name`() {
        val customer1 = book.createCustomer(1, name = "paul atreides")
        val customer2 = book.createCustomer(2, name = "Paul Atreides")
        book.createCustomer(3, name = "Paul Smith")

        book.listCustomers(CustomerName.of("Paul atreides")).shouldContainExactlyInAnyOrder(customer1, customer2)
    }

    @Test
    fun `list customers - by exact email`() {
        val customer1 = book.createCustomer(1, email = "foo@bar.com")
        book.createCustomer(2, email = "toll@troll.com")
        val customer3 = book.createCustomer(3, email = "foo@bar.com")

        book.listCustomers(customer1.email).shouldContainExactlyInAnyOrder(customer1, customer3)
    }

    @Test
    fun `list customers - by partial email (not supported)`() {
        book.createCustomer(1, email = "foo@bar.com")
        book.createCustomer(2, email = "foo@bar.ca")
        book.createCustomer(3, email = "foo@bar.io")

        book.listCustomers(EmailAddress.of("foo@bar")).shouldBeEmpty()
    }

    @Test
    fun `list customers - by case-insensitive email`() {
        val customer1 = book.createCustomer(1, email = "foo@bar.com")
        book.createCustomer(2, email = "toll@troll.com")
        val customer3 = book.createCustomer(3, email = "FOO@bar.com")

        book.listCustomers(customer1.email).shouldContainExactlyInAnyOrder(customer1, customer3)
    }

    @Test
    fun `list customers - by name and email`() {
        val customer1 = book.createCustomer(1, name = "Leto Atreides", email = "leo@atreidies.arakis")
        book.createCustomer(2, name = "Leto Atreides", email = "leto@atreides.caladan")
        book.createCustomer(3, name = "Leto Atreides II", email = "leto@atreides.arakis")

        book.listCustomers(customer1.name, customer1.email).shouldContainExactlyInAnyOrder(customer1)
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
