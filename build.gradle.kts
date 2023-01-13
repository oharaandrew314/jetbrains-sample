plugins {
    kotlin("jvm") version "1.8.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    application
}

application {
    mainClass.set("io.andrewohara.jetbrains.sample.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("io.ktor:ktor-bom:2.2.1"))
    implementation(platform("dev.forkhandles:forkhandles-bom:2.3.0.0"))
    implementation(platform("org.jetbrains.kotlinx:kotlinx-serialization-bom:1.4.1"))
    implementation(platform("org.jetbrains.exposed:exposed-bom:0.40.1"))

    implementation(kotlin("stdlib-jdk8"))
    implementation("org.slf4j:slf4j-simple:2.0.6")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-resources")
    implementation("org.jetbrains.exposed:exposed-jdbc")
    implementation("dev.forkhandles:result4k")
    implementation("dev.forkhandles:values4k")

    testImplementation(kotlin("test"))
    testRuntimeOnly("com.h2database:h2:2.1.214")
    testImplementation("io.kotest:kotest-assertions-core-jvm:5.5.4")
    testImplementation("io.kotest.extensions:kotest-assertions-ktor:1.0.3")
    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation("io.ktor:ktor-client-content-negotiation")
}

tasks.test {
    useJUnitPlatform()
}