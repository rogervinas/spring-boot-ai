package com.rogervinas

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.ai.document.Document
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.beans.factory.annotation.Value
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
        objectMapper: ObjectMapper,
        @Value("\${spring.ai.vectorstore.pgvector.table-name:vector_store}") tableName: String
    ) = ApplicationRunner {
        val logger = LoggerFactory.getLogger(ChatServerApplication::class.java)
        val vectorStoreCount = vectorStoreCount(jdbcTemplate, tableName)
        if (vectorStoreCount == 0) {
            logger.info("Initializing vector store ...")
            val cities = ClassPathResource("cities.json").inputStream.use {
                objectMapper.readValue(it, Array<City>::class.java).toList()
            }
            cities.forEach { city ->
                logger.info("Adding ${city.name} to vector store ...")
                val document = Document("name: ${city.name} country: ${city.country} description: ${city.description}")
                vectorStore.add(listOf(document))
            }
            logger.info("Vector store initialized with ${cities.size} cities")
        } else {
            logger.info("Vector store already contains $vectorStoreCount cities")
        }
    }

    private fun vectorStoreCount(jdbcTemplate: JdbcTemplate, tableName: String) =
        jdbcTemplate.queryForObject("SELECT COUNT(*) FROM $tableName", Int::class.java)
}

fun main(args: Array<String>) {
    runApplication<ChatServerApplication>(*args)
}
