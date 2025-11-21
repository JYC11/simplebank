plugins {
    kotlin("jvm") version "2.2.20"
    kotlin("plugin.spring") version "2.2.20"
    id("org.springframework.boot") version "4.0.0"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.flywaydb.flyway") version "11.13.2"
    id("dev.monosoul.jooq-docker") version "7.0.15"
}

group = "org.example"
version = "0.0.1-SNAPSHOT"
description = "simplebank"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

val dbHost = System.getenv("DB_HOST") ?: "localhost"
val dbName = System.getenv("DB_NAME") ?: "simplebank"
val dbPort = System.getenv("DB_PORT") ?: "5432"
val dbUsername = System.getenv("DB_USERNAME") ?: "admin"
val dbPassword = System.getenv("DB_PASSWORD") ?: "1234"

flyway {
    url = "jdbc:postgresql://$dbHost:$dbPort/$dbName"
    user = dbUsername
    password = dbPassword
}

val flywayVersion = "11.13.2"
val jooqVersion = "3.20.9"
val postgresVersion = "42.7.4"
val testContainersVersion = "1.21.3"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("org.springframework.boot:spring-boot-starter-jooq")
    implementation("org.jooq:jooq:$jooqVersion")
    implementation("org.jooq:jooq-kotlin:$jooqVersion")
    implementation("org.jooq:jooq-codegen:$jooqVersion")

    jooqCodegen(project(":jooq-custom"))
    jooqCodegen("org.postgresql:postgresql:$postgresVersion")
    jooqCodegen("org.flywaydb:flyway-core:$flywayVersion")
    jooqCodegen("org.flywaydb:flyway-database-postgresql:$flywayVersion")

    implementation("org.postgresql:postgresql:$postgresVersion")
    implementation("org.flywaydb:flyway-core:$flywayVersion")
    implementation("org.flywaydb:flyway-database-postgresql:$flywayVersion")


    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.testcontainers:postgresql:$testContainersVersion")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks {
    generateJooqClasses {
        outputDirectory.set(project.layout.projectDirectory.dir("build/generated"))
        usingJavaConfig {
            withName("org.jooq.codegen.KotlinGenerator")
            generatorConfig.apply {
                database.apply {
                    inputSchema = "public"
                    excludes = "flyway_schema_history"
                    isUnsignedTypes = false
                }
                generate.apply {
                    isRoutines = false
                    isIndexes = false
                    isKeys = false
                    isPojosAsKotlinDataClasses = true
                    isFluentSetters = true
                    isDeprecated = false
                    isJavaTimeTypes = true
                    isRecords = true
                    isDaos = true
                }
                strategy.name = "org.example.CustomGeneratorStrategy"
            }
        }
    }
}

tasks.named("compileKotlin") {
    dependsOn(tasks.named("generateJooqClasses"))
}