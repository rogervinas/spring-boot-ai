package com.rogervinas

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.rogervinas.chat.ChatController
import com.rogervinas.chat.ChatService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.reactive.server.WebTestClient
import java.util.UUID

@WebMvcTest(ChatController::class)
@AutoConfigureWebTestClient
class ChatControllerTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    @MockitoBean
    lateinit var chatService: ChatService

    @Test
    fun `should serve chat requests`() {
        val chatId = UUID.randomUUID().toString()
        val chatQuestion = "What is the weather like in Paris tomorrow?"
        val chatAnswer = "Sunny with a high of 25°C"

        doReturn(chatAnswer)
            .whenever(chatService)
            .chat(chatId, chatQuestion)

        webTestClient.post()
            .uri("/$chatId/chat")
            .contentType(APPLICATION_FORM_URLENCODED)
            .bodyValue("question=$chatQuestion")
            .exchange()
            .expectStatus().isOk
            .expectBody(String::class.java)
            .consumeWith { response ->
                assertThat(response.responseBody).isEqualTo(chatAnswer)
            }
    }
}
