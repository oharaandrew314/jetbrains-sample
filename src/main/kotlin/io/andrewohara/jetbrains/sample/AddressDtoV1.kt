package io.andrewohara.jetbrains.sample

import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class AddressDtoV1(
    val streetName: String,
    val city: String,
    val postCode: String,
    val state: String,
    val country: String
)

@Serializable
data class CustomerDtoV1(
    val id: UUID,
    val name: String,
    val email: String,
    val addresses: List<AddressDtoV1>
)