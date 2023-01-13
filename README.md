# Jetbrains Sample

Toy Address Book project built with Ktor, Exposed, and kotlinx-serialization

## Run the API

### Environment Variables

- PORT: HTTP port; default 8080

```shell
$ sh gradlew run
```

## Run Tests

```shell
$ sh gradlew check
```

## Package for Deployment

```shell
$ sh gradlew shadowJar
```