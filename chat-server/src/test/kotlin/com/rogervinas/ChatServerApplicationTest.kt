package com.rogervinas

import com.rogervinas.chat.ChatService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.evaluation.EvaluationRequest
import org.springframework.ai.evaluation.RelevancyEvaluator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Testcontainers
import java.util.UUID


@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class ChatServerApplicationTest {

    val logger = LoggerFactory.getLogger(ChatServerApplicationTest::class.java)

    @Autowired
    lateinit var chatClientBuilder: ChatClient.Builder

    @Autowired
    lateinit var chatService: ChatService

    @ParameterizedTest
    @ValueSource(
        strings = [
            "I want to go to a city with a beach. Where should I go?",
        ]
    )
    fun `should give relevant information`(question: String) {
        logger.info("Question: $question")

        val relevancyEvaluation = RelevancyEvaluator(chatClientBuilder)

        val chatId = UUID.randomUUID().toString()
        val chatResponse = chatService.chat(chatId, question)

        logger.info("Chat response: $chatResponse")

        val evaluationRequest = EvaluationRequest(question, chatResponse)
        val evaluationResult = relevancyEvaluation.evaluate(evaluationRequest)

        logger.info("Evaluation result: $evaluationResult")

        assertThat(evaluationResult.isPass).isTrue
    }
}
