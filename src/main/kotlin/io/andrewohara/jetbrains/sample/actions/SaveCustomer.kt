package io.andrewohara.jetbrains.sample.actions

import io.andrewohara.jetbrains.sample.AddressTable
import io.andrewohara.jetbrains.sample.Customer
import io.andrewohara.jetbrains.sample.CustomerData
import io.andrewohara.jetbrains.sample.CustomerId
import io.andrewohara.jetbrains.sample.CustomerTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction

fun interface SaveCustomer: (CustomerData) -> Customer {
    companion object
}

fun SaveCustomer.Companion.exposed(database: Database) = SaveCustomer { data ->
    transaction(database) {
        val id = CustomerTable.insertAndGetId { row ->
            row[name] = data.name.value
            row[email] = data.email.value
        }

        for (address in data.addresses) {
            AddressTable.insert { row ->
                row[customerId] = id
                row[streetName] = address.streetName
                row[city] = address.city
                row[postCode] = address.postCode
                row[state] = address.state
                row[country] = address.country
            }
        }

        Customer(
            id = CustomerId.of(id.value),
            name = data.name,
            email = data.email,
            addresses = data.addresses
        )
    }
}