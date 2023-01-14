package io.andrewohara.jetbrains.sample

import dev.forkhandles.values.NonEmptyStringValueFactory
import dev.forkhandles.values.StringValue
import dev.forkhandles.values.UUIDValue
import dev.forkhandles.values.UUIDValueFactory
import java.util.UUID

class CustomerId private constructor(value: UUID): UUIDValue(value) {
    companion object: UUIDValueFactory<CustomerId>(::CustomerId)
}

class CustomerName private constructor(value: String): StringValue(value) {
    companion object: NonEmptyStringValueFactory<CustomerName>(::CustomerName)
}

class EmailAddress private constructor(value: String): StringValue(value) {
    companion object: NonEmptyStringValueFactory<EmailAddress>(::EmailAddress)
}

data class Customer(
    val id: CustomerId,
    val name: CustomerName,
    val email: EmailAddress,
    val addresses: List<Address>
)

data class CustomerData(
    val name: CustomerName,
    val email: EmailAddress,
    val addresses: List<Address>
)

data class Address(
    val streetName: String,
    val city: String,
    val postCode: String,
    val state: String,
    val country: String
)