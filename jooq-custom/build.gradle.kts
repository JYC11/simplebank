plugins {
    id("java")
}

group = "org.example"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

val jooqVersion = "3.20.8"


dependencies {
    implementation("org.jooq:jooq-codegen:${jooqVersion}")
    implementation("org.jooq:jooq-meta:${jooqVersion}")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}
