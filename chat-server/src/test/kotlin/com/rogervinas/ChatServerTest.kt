package com.rogervinas

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.evaluation.EvaluationRequest
import org.springframework.ai.evaluation.FactCheckingEvaluator
import org.springframework.ai.evaluation.RelevancyEvaluator
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.test.context.ActiveProfiles
import java.time.Instant
import java.time.temporal.ChronoUnit.DAYS
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.E

@SpringBootTest
@ActiveProfiles("test")
class ChatServerTest {

    @Autowired
    lateinit var chatClientBuilder: ChatClient.Builder

    @Autowired
    lateinit var chatClient: ChatClient

    @Test
    fun testEvaluation() {

        val relevancyEvaluation = RelevancyEvaluator(chatClientBuilder)

        val userText = "Can you create an appointment to adopt a dog named Pepsy with id 1234?"
        val chatResponse = chatClient.prompt(userText).call().content()

        println("Chat response: $chatResponse")

        val evaluationRequest = EvaluationRequest(userText, chatResponse)

        println("Evaluation request: $evaluationRequest")
        val evaluationResult = relevancyEvaluation.evaluate(evaluationRequest)

        println("Evaluation result: $evaluationResult")

        val factCheckingEvaluator = FactCheckingEvaluator(chatClientBuilder)
        val factCheckingEvaluation = EvaluationRequest(
            chatResponse,
            "An appointment to adopt a dog named Pepsy with id 1234 has been created on 2023-10-01T12:00:00Z"
        )

        println("Fact checking request: $factCheckingEvaluation")
        val factCheckingResult = factCheckingEvaluator.evaluate(factCheckingEvaluation)

        println("Fact checking result: $factCheckingResult")

        assertThat(evaluationResult.isPass).isTrue
        assertThat(factCheckingResult.isPass).isTrue
    }
}

@Configuration
class TestToolConfiguration {
    @Bean
    fun testToolProvider(testTool: TestTool): MethodToolCallbackProvider {
        return MethodToolCallbackProvider.builder().toolObjects(testTool).build()
    }

    @Bean
    fun testTool() = TestTool()
}

class TestTool {
    private val appointments = mutableListOf<String>()

    fun clearAppointments() {
        appointments.clear()
    }

    fun getAppointments(): List<String> {
        return appointments
    }

    @Tool(
        description = "schedule an appointment to adopt a dog at the Pooch Palace dog adoption agency"
    )
    fun scheduleDogAdoptionAppointment(
        @ToolParam(description = "the id of the dog") id: Int,
        @ToolParam(description = "the name of the dog") name: String
    ): String {
        println("Confirming the appointment: for dog $id named $name")
        return "2023-10-01T12:00:00Z"
    }
}
