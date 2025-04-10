plugins {
    val kotlinVersion = "2.1.20"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    id("org.springframework.boot") version "3.4.4"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.rogervinas"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

val springAiVersion = "1.0.0-M6"
val flywayVersion = "11.6.0"

dependencies {
    implementation("org.springframework.ai:spring-ai-mcp-client-spring-boot-starter")
    implementation("org.springframework.ai:spring-ai-bedrock-converse-spring-boot-starter")
    implementation("org.springframework.ai:spring-ai-bedrock-ai-spring-boot-starter")

    implementation("org.springframework.ai:spring-ai-pgvector-store-spring-boot-starter")
    runtimeOnly("org.postgresql:postgresql")

    implementation("org.flywaydb:flyway-core:$flywayVersion")
    implementation("org.flywaydb:flyway-database-postgresql:$flywayVersion")

    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")

    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation(platform("org.junit:junit-bom:5.12.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:$springAiVersion")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
