package com.rogervinas

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.whenever
import com.rogervinas.chat.ChatService
import com.rogervinas.configuration.BookingTestService
import com.rogervinas.evaluator.TestEvaluator
import com.rogervinas.tools.WeatherService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito.doAnswer
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.evaluation.EvaluationRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.testcontainers.containers.ComposeContainer
import org.testcontainers.containers.wait.strategy.Wait.forLogMessage
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.io.File
import java.time.Clock
import java.time.LocalDate
import java.util.UUID


@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test", "ollama")
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@Testcontainers
class ChatServerApplicationTest {

    companion object {
        @Container
        @JvmStatic
        val container = ComposeContainer(File("docker-compose.yml"))
            .withLocalCompose(true)
            .withExposedService("vectordb", 5432, forLogMessage(".*database system is ready to accept connections.*", 1))
            .withExposedService("ollama", 11434, forLogMessage(".*inference compute.*", 1))
    }

    @Autowired
    lateinit var chatClientBuilder: ChatClient.Builder

    @Autowired
    lateinit var clock: Clock

    @Autowired
    lateinit var chatService: ChatService

    @MockitoBean
    lateinit var weatherService: WeatherService

    @MockitoBean
    lateinit var bookingTestService: BookingTestService

    @Test
    @Order(0)
    @EnabledIfCI
    fun `should be up and running`() {
        val chatId = UUID.randomUUID().toString()
        val chatResponse = chatService.chat(chatId, "Hello!")

        assertThat(chatResponse).isNotNull()
    }

    @Test
    @Order(0)
    fun `should have tools available`() {
        val chatId = UUID.randomUUID().toString()
        val chatResponse = chatService.chat(chatId, "What tools do you have available?")

        val evaluationResult = TestEvaluator(chatClientBuilder) { evaluationRequest, userSpec ->
            userSpec.text(
                """
                Your task is to evaluate if the answer given by an AI agent to a human user matches the claim.
                Return YES if the answer matches the claim and NO if it does not.
                After returning YES or NO, explain why.
                Answer: {answer}
                Claim: {claim}
            """.trimIndent()
            )
                .param("answer", evaluationRequest.responseContent)
                .param("claim", evaluationRequest.userText)
        }.evaluate(EvaluationRequest("The AI agent has at least these three tools available: get date, get weather and book accommodation", chatResponse))

        assertThat(evaluationResult.isPass).isTrue.withFailMessage { evaluationResult.feedback }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "yesterday, was, 2025-04-14",
            "today, is, 2025-04-15",
            "tomorrow, will be, 2025-04-16",
        ]
    )
    @Order(1)
    @DisabledIfCI
    fun `should use clock tool`(date: String, be: String, expectedDate: String) {
        val chatId = UUID.randomUUID().toString()
        val chatResponse = chatService.chat(chatId, "What day $be $date?")

        val evaluationResult = TestEvaluator(chatClientBuilder) { evaluationRequest, userSpec ->
            userSpec.text(
                """
                Your task is to evaluate if the answer given by an AI agent to a human user matches the claim.
                Return YES if the answer matches the claim and NO if it does not.
                After returning YES or NO, explain why.
                Ignore date formatting differences when comparing dates.
                Assume that today is ${LocalDate.now(clock)}.
                Answer: {answer}
                Claim: {claim}
            """.trimIndent()
            )
                .param("answer", evaluationRequest.responseContent)
                .param("claim", evaluationRequest.userText)
        }.evaluate(EvaluationRequest("$date $be $expectedDate", chatResponse))

        assertThat(evaluationResult.isPass).isTrue.withFailMessage { evaluationResult.feedback }
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "I want to go to a city with a beach. Where should I go?",
            "I would like to visit a city with great history and culture. Any suggestions?",
        ]
    )
    @DisabledIfCI
    fun `should give relevant information`(question: String) {
        val chatId = UUID.randomUUID().toString()
        val chatResponse = chatService.chat(chatId, question)

        val evaluationResult = TestEvaluator(chatClientBuilder) { evaluationRequest, userSpec ->
            userSpec.text(
                """
                Your task is to evaluate if the answer given by an AI agent is relevant to the question from a human user.
                Return YES if the answer is relevant to the question and NO if it is not.
                After returning YES or NO, explain why.
                Question: {question}
                Answer: {answer}
            """.trimIndent()
            )
                .param("question", evaluationRequest.userText)
                .param("answer", evaluationRequest.responseContent)
        }.evaluate(EvaluationRequest(question, chatResponse))

        assertThat(evaluationResult.isPass).isTrue.withFailMessage { evaluationResult.feedback }
    }

    @ParameterizedTest
    @ValueSource(strings = ["Barcelona", "Madrid"])
    @DisabledIfCI
    fun `should have memory for each chat`(favouriteCity: String) {
        val chatId = UUID.randomUUID().toString()
        val chatResponseIgnored = chatService.chat(chatId, "My favourite city is $favouriteCity, what do you think?")
        val chatResponse = chatService.chat(chatId, "What is my favourite city?")

        val evaluationResult = TestEvaluator(chatClientBuilder) { evaluationRequest, userSpec ->
            userSpec.text(
                """
                Your task is to evaluate if the answer given by an AI agent to a human user matches the claim.
                When the answer says "you" it is the AI agent referring to the human user.
                Return YES if the answer matches the claim and NO if it does not.
                After returning YES or NO, explain why.
                Answer: {answer}
                Claim: {claim}
            """.trimIndent()
            )
                .param("answer", evaluationRequest.responseContent)
                .param("claim", evaluationRequest.userText)
        }.evaluate(EvaluationRequest("Human user favourite city is $favouriteCity", chatResponse))

        assertThat(evaluationResult.isPass).isTrue.withFailMessage { evaluationResult.feedback }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "Tokyo, today, partly sunny and warm with a high of 24ºC, 2025-04-15, 2025-04-15",
            "Dubai, tomorrow, hazy and extremely hot with a high of 41°C, 2025-04-16, 2025-04-16",
            "Toronto, this weekend, mostly cloudy with a high of 13°C, 2025-04-19, 2025-04-20",
        ]
    )
    @DisabledIfCI
    fun `should get weather conditions`(city: String, date: String, weather: String, date1: LocalDate, date2: LocalDate) {
        val cityCaptor = argumentCaptor<String>()
        val dateCaptor = argumentCaptor<LocalDate>()
        doAnswer { c -> "The weather in ${c.getArgument<String>(0)} on ${c.getArgument<LocalDate>(1)} is $weather" }
            .whenever(weatherService).getWeather(cityCaptor.capture(), dateCaptor.capture())

        val chatId = UUID.randomUUID().toString()
        val chatResponse = chatService.chat(chatId, "How is the weather like in $city $date?")

        val evaluationResult = TestEvaluator(chatClientBuilder) { evaluationRequest, userSpec ->
            userSpec.text(
                """
                Your task is to evaluate if the answer given by an AI agent to a human user matches the claim.
                Return YES if the answer matches the claim and NO if it does not.
                After returning YES or NO, explain why.
                Assume that today is ${LocalDate.now(clock)}.
                Assume that the AI agent has information about the weather for a given city and date.
                Answer: {answer}
                Claim: {claim}
            """.trimIndent()
            )
                .param("answer", evaluationRequest.responseContent)
                .param("claim", evaluationRequest.userText)
        }.evaluate(EvaluationRequest("Weather for $city $date is or will be $weather", chatResponse))

        assertThat(evaluationResult.isPass).isTrue.withFailMessage { evaluationResult.feedback }

        assertThat(cityCaptor.allValues).allSatisfy { assertThat(it).isEqualTo(city) }
        assertThat(dateCaptor.allValues).containsExactlyInAnyOrderElementsOf(listOf(date1, date2).distinct())
    }

    @Test
    @DisabledIfCI
    fun `should fail getting weather conditions`() {
        doAnswer { c -> "Sorry I do not have weather information at the moment" }
            .whenever(weatherService).getWeather(any(), any())

        val chatId = UUID.randomUUID().toString()
        val chatResponse = chatService.chat(chatId, "How is the weather like in Venice today?")

        val evaluationResult = TestEvaluator(chatClientBuilder) { evaluationRequest, userSpec ->
            userSpec.text(
                """
                Your task is to evaluate if the answer given by an AI agent to a human user matches the claim.
                Return YES if the answer matches the claim and NO if it does not.
                After returning YES or NO, explain why.
                Answer: {answer}
                Claim: {claim}
            """.trimIndent()
            )
                .param("answer", evaluationRequest.responseContent)
                .param("claim", evaluationRequest.userText)
        }.evaluate(EvaluationRequest("Weather for Venice today is unknown", chatResponse))

        assertThat(evaluationResult.isPass).isTrue.withFailMessage { evaluationResult.feedback }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "Istanbul, today for one week, 2025-04-15, 2025-04-22",
            "Marrakech, tomorrow for 3 days, 2025-04-16, 2025-04-19",
            "Berlin, this weekend, 2025-04-19, 2025-04-21",
        ]
    )
    @DisabledIfCI
    fun `should book accommodation`(city: String, date: String, checkInDate: LocalDate, checkOutDate: LocalDate) {
        val cityCaptor = argumentCaptor<String>()
        val checkInDateCaptor = argumentCaptor<LocalDate>()
        val checkOutDateCaptor = argumentCaptor<LocalDate>()
        doAnswer { c -> "Your accommodation has been booked in ${c.getArgument<String>(0)} from ${c.getArgument<LocalDate>(1)} to ${c.getArgument<LocalDate>(2)}" }
            .whenever(bookingTestService).book(cityCaptor.capture(), checkInDateCaptor.capture(), checkOutDateCaptor.capture())

        val chatId = UUID.randomUUID().toString()
        val chatResponse = chatService.chat(chatId, "Can you book accommodation for $city $date?")

        val evaluationResult = TestEvaluator(chatClientBuilder) { evaluationRequest, userSpec ->
            userSpec.text(
                """
                Your task is to evaluate if the answer given by an AI agent to a human user matches the claim.
                Return YES if the answer matches the claim and NO if it does not.
                After returning YES or NO, explain why.
                Assume that today is ${LocalDate.now(clock)}.
                Answer: {answer}
                Claim: {claim}
            """.trimIndent()
            )
                .param("answer", evaluationRequest.responseContent)
                .param("claim", evaluationRequest.userText)
        }.evaluate(EvaluationRequest("Accommodation has been booked for $city from $checkInDate to $checkOutDate", chatResponse))

        assertThat(evaluationResult.isPass).isTrue.withFailMessage { evaluationResult.feedback }

        assertThat(cityCaptor.allValues).singleElement().isEqualTo(city)
        assertThat(checkInDateCaptor.allValues).singleElement().isEqualTo(checkInDate)
        assertThat(checkOutDateCaptor.allValues).singleElement().isEqualTo(checkOutDate)
    }

    @Test
    @DisabledIfCI
    fun `should fail booking accommodation`() {
        doAnswer { c -> "Unfortunately, the accommodation is fully booked for the selected dates" }
            .whenever(bookingTestService).book(any(), any(), any())

        val chatId = UUID.randomUUID().toString()
        val chatResponse = chatService.chat(chatId, "Can you book accommodation for Milan two weeks from now for 3 days?")

        val evaluationResult = TestEvaluator(chatClientBuilder) { evaluationRequest, userSpec ->
            userSpec.text(
                """
                Your task is to evaluate if the answer given by an AI agent to a human user matches the claim.
                Return YES if the answer matches the claim and NO if it does not.
                After returning YES or NO, explain why.
                Answer: {answer}
                Claim: {claim}
            """.trimIndent()
            )
                .param("answer", evaluationRequest.responseContent)
                .param("claim", evaluationRequest.userText)
        }.evaluate(EvaluationRequest("Accommodation cannot be booked for the requested dates", chatResponse))

        assertThat(evaluationResult.isPass).isTrue.withFailMessage { evaluationResult.feedback }
    }
}

@DisabledIfEnvironmentVariable(named = "CI", matches = "true")
annotation class DisabledIfCI

@EnabledIfEnvironmentVariable(named = "CI", matches = "true")
annotation class EnabledIfCI
