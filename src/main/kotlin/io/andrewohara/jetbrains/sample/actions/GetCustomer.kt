package io.andrewohara.jetbrains.sample.actions

import io.andrewohara.jetbrains.sample.Address
import io.andrewohara.jetbrains.sample.AddressTable
import io.andrewohara.jetbrains.sample.Customer
import io.andrewohara.jetbrains.sample.CustomerId
import io.andrewohara.jetbrains.sample.CustomerName
import io.andrewohara.jetbrains.sample.CustomerTable
import io.andrewohara.jetbrains.sample.EmailAddress
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

fun interface GetCustomer: (CustomerId) -> Customer? {
    companion object
}

fun GetCustomer.Companion.exposed(database: Database) = GetCustomer { id ->
    transaction(database) {
        val addresses = AddressTable
            .select { AddressTable.customerId eq id.value }
            .map { row ->
                Address(
                    streetName = row[AddressTable.streetName],
                    city = row[AddressTable.city],
                    postCode = row[AddressTable.postCode],
                    state = row[AddressTable.state],
                    country = row[AddressTable.country]
                )
            }

        CustomerTable
            .select { CustomerTable.id eq id.value }
            .limit(1)
            .map { row ->
                Customer(
                    id = CustomerId.of(row[CustomerTable.id].value),
                    name = CustomerName.of(row[CustomerTable.name]),
                    email = EmailAddress.of(row[CustomerTable.email]),
                    addresses = addresses
                )
            }
            .firstOrNull()
    }
}