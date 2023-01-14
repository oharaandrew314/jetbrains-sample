package io.andrewohara.jetbrains.sample

import kotlinx.serialization.Serializable

@Serializable
data class CustomerDtoV1(
    val id: String,
    val name: String,
    val email: String,
    val addresses: Set<AddressDtoV1>
)

@Serializable
data class CustomerDataDtoV1(
    val name: String,
    val email: String,
    val addresses: Set<AddressDtoV1>
)

@Serializable
data class AddressDtoV1(
    val streetName: String,
    val city: String,
    val postCode: String,
    val state: String,
    val country: String
)

fun Customer.toDtoV1() = CustomerDtoV1(
    id = id.value.toString(),
    name = name.value,
    email = email.value,
    addresses = addresses.map { it.toDtoV1() }.toSet()
)

fun Address.toDtoV1() = AddressDtoV1(
    streetName = streetName,
    city = city,
    postCode = postCode,
    state = state,
    country = country
)

fun CustomerDataDtoV1.toInternal() = CustomerData(
    name = CustomerName.of(name),
    email = EmailAddress.of(email),
    addresses = addresses.map { it.toInternal() }
)

fun AddressDtoV1.toInternal() = Address(
    streetName = streetName,
    city = city,
    postCode = postCode,
    state = state,
    country = country
)
