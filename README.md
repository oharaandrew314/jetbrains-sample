# Jetbrains Sample

Toy Address Book project built with Ktor, Exposed, and kotlinx-serialization.

## Serve the API

There are a few optional environment variables:
- **PORT**: default *8080*
- **DB_IN_MEMORY**: [*true*, *false*], default *true*
- **DB_NAME**: default *./addressBook*

```shell
$ sh gradlew run
```

## API Routes

| Operation     | Method | Path               | Inputs                                                                 | Outputs                      |
|---------------|--------|--------------------|------------------------------------------------------------------------|------------------------------|
| ListCustomers | GET    | /v1/customers      | - name: String (query, optional)<br/>- email: String (query, optional) | - List<CustomerDtoV1> (body) |
| GetCustomer   | GET    | /v1/customers/{id} | - id: UUID (path)                                                      | - CustomerDtoV1 (body)       |
| SaveCustomer  | POST   | /v1/customers      | - CustomerDataDtoV1 (body)                                             | - CustomerDtoV1 (body)       |


## Run Tests

```shell
$ sh gradlew check
```

## Package Uber Jar

```shell
$ sh gradlew shadowJar
```