import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.PASSED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import java.util.Properties

plugins {
    val kotlinVersion = "2.1.21"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    id("org.springframework.boot") version "3.4.5"
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

val springAiVersion = "1.0.0-RC1"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.ai:spring-ai-starter-mcp-client")
    implementation("org.springframework.ai:spring-ai-advisors-vector-store")

    // ollama
    implementation("org.springframework.ai:spring-ai-starter-model-ollama")

    // bedrock
    // implementation("org.springframework.ai:spring-ai-starter-model-bedrock")
    // implementation("org.springframework.ai:spring-ai-starter-model-bedrock-converse")

    implementation("org.springframework.ai:spring-ai-starter-vector-store-pgvector")
    runtimeOnly("org.postgresql:postgresql")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8")

    implementation("org.jetbrains.kotlin:kotlin-reflect")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation(platform("org.junit:junit-bom:5.12.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("org.mockito:mockito-core:5.17.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.17.0")
    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")

    testImplementation("org.testcontainers:junit-jupiter:1.21.0")
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
