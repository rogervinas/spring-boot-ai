package com.rogervinas

import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Service
import java.time.Instant
import java.time.temporal.ChronoUnit.DAYS

@SpringBootApplication
class McpServerApplication {
    @Bean
    fun serviceToolCallbackProvider(scheduler: DogAdoptionAppointmentScheduler): ToolCallbackProvider {
        return MethodToolCallbackProvider.builder()
            .toolObjects(scheduler)
            .build()
    }
}

@Service
class DogAdoptionAppointmentScheduler {
    @Tool(
        description = "schedule an appointment to adopt a dog at the Pooch Palace dog adoption agency"
    )
    fun scheduleDogAdoptionAppointment(
        @ToolParam(description = "the id of the dog") id: Int,
        @ToolParam(description = "the name of the dog") name: String
    ): String {
        val instant = Instant.now().plus(3, DAYS)
        println("confirming the appointment: $instant for dog $id named $name")
        return instant.toString()
    }
}

fun main(args: Array<String>) {
    runApplication<McpServerApplication>(*args)
}
