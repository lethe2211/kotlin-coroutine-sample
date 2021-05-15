# Kotlin Coroutines samples

## Dependencies
* JDK 11
* Kotlin 1.4
* Gradle 6
* Docker >= 19

## Run the app

### Simply run

```shell script
$ ./gradlew run
```

## Build the app

### Build a JAR archive

```shell script
$ ./gradlew clean build
```

### Build a Docker image

```shell script
$ docker build .
  # Note that it will take around 5-10 min if you run it for the first time
```

## Format the code

```shell script
$ ./gradlew ktlintFormat
```

## Run unit tests

```shell script
$ ./gradlew test
```