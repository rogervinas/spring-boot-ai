import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import java.util.Properties

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

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.ai:spring-ai-mcp-client-spring-boot-starter")

    // ollama
    // implementation("org.springframework.ai:spring-ai-ollama-spring-boot-starter")

    // bedrock
    implementation("org.springframework.ai:spring-ai-bedrock-converse-spring-boot-starter")
    implementation("org.springframework.ai:spring-ai-bedrock-ai-spring-boot-starter")

    implementation("org.springframework.ai:spring-ai-pgvector-store-spring-boot-starter")
    runtimeOnly("org.postgresql:postgresql")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.6")

    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.ai:spring-ai-mcp-server-webflux-spring-boot-starter")

    testImplementation(platform("org.junit:junit-bom:5.12.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.mockito:mockito-core:5.17.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.17.0")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")

    testImplementation("org.testcontainers:junit-jupiter:1.20.6")
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
    testLogging {
        events(PASSED, SKIPPED, FAILED)
    }
    setSystemProperties { systemProperty(it.first, it.second) }
}

tasks.withType<JavaExec> {
    setSystemProperties { systemProperty(it.first, it.second) }
}

private fun setSystemProperties(setSystemProperty: (Pair<String, Any>) -> Unit) {
    val systemPropertiesFile = project.rootProject.file("system.properties")
    if (systemPropertiesFile.exists()) {
        systemPropertiesFile.inputStream().use { inputStream ->
            Properties().apply {
                load(inputStream)
            }.forEach {
                setSystemProperty(it.key.toString() to it.value)
            }
        }
    }
}
