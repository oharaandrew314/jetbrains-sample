package io.andrewohara.jetbrains.sample

import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.util.UUID

class AddressBookApplicationContract {

    private val app = AddressBookApplication(dbInMemory = true, dbName = UUID.randomUUID().toString())

    @Test
    fun `get customer - not found`() {
        val id = CustomerId.parse("6a05d083-ca92-454f-aade-0b14ddff4133")
        app.getCustomer(id).shouldBeNull()
    }

    @Test
    fun `save and get customer - found`() {
        val customer = customerData(5, address(6), address(7), address(8))
            .let(app.saveCustomer)

        app.getCustomer(customer.id) shouldBe customer
    }
}
