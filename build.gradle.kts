plugins {
    kotlin("jvm") version "1.4.0"
    application

    // Shadow Plugin (to generate a Fat JAR)
    id("com.github.johnrengelman.shadow") version "6.1.0"

    // ktlint Plugin
    id("org.jlleitschuh.gradle.ktlint") version "9.4.1"
}

group = "org.example"
version = "1.0-SNAPSHOT"
repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // Kotlin Coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.4.3")

    // Coroutine test library
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.4.3")

    // JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")

    // Mockito
    testImplementation("org.mockito:mockito-core:3.10.0")
    testImplementation("org.mockito.kotlin:mockito-kotlin:3.2.0")

    // MockK
    testImplementation("io.mockk:mockk:1.11.0")
}

application {
    mainClassName = "org.example.demo.MainKt"
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    test {
        useJUnitPlatform() // Enable JUnit 5 support of Gradle
    }
}
