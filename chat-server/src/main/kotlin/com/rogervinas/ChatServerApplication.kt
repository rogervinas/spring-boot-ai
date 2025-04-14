package com.rogervinas

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.jdbc.core.JdbcTemplate

@SpringBootApplication
class ChatServerApplication

@Configuration
class VectorStoreConfiguration {

    data class City(val name: String, val country: String, val description: String)

    @Bean
    fun vectorStoreInitializer(
        vectorStore: VectorStore,
        jdbcTemplate: JdbcTemplate,
        objectMapper: ObjectMapper
    ) = ApplicationRunner {
        if (isVectorStoreEmpty(jdbcTemplate)) {
            val logger = LoggerFactory.getLogger(ChatServerApplication::class.java)
            logger.info("Initializing vector store ...")
            val cities = ClassPathResource("cities.json").inputStream.use {
                objectMapper.readValue(it, Array<City>::class.java).toList()
            }
            cities.forEach { city ->
                val document = Document("name: ${city.name} country: ${city.country} description: ${city.description}")
                vectorStore.add(listOf(document))
            }
            logger.info("Vector store initialized with ${cities.size} documents")
        }
    }

    private fun isVectorStoreEmpty(jdbcTemplate: JdbcTemplate) =
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vector_store", Int::class.java) == 0
}

fun main(args: Array<String>) {
    runApplication<ChatServerApplication>(*args)
}
