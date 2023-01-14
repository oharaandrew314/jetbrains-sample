package io.andrewohara.jetbrains.sample

fun customerData(number: Int, vararg addresses: Address) = CustomerData(
    name = CustomerName.of("customer $number"),
    email = EmailAddress.of("customer$number@fakemail.xyz"),
    addresses = addresses.toList()
)

fun address(number: Int) = Address(
    streetName = "$number my street",
    city = "city $number",
    postCode = "postcode $number",
    state = "state $number",
    country = "country $number"
)