# Jetbrains Sample

Toy Address Book project built with Ktor, Exposed, and kotlinx-serialization.

## Run the API

There are a few optional environment variables:
- **PORT**: default *8080*
- **DB_IN_MEMORY**: [*true*, *false*], default *true*
- **DB_NAME**: default *./addressBook*

```shell
$ sh gradlew run
```

## Run Tests

```shell
$ sh gradlew check
```

## Package Uber Jar

```shell
$ sh gradlew shadowJar
```