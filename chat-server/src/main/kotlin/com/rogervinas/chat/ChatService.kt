package com.rogervinas.chat

import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor
import org.springframework.ai.chat.memory.ChatMemory
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository
import org.springframework.ai.chat.memory.MessageWindowChatMemory
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDate

@Service
class ChatService(vectorStore: VectorStore, private val clock: Clock, private val chatClient: ChatClient) {

    private val logger = LoggerFactory.getLogger(ChatService::class.java)
    private val questionAnswerAdvisor = QuestionAnswerAdvisor.builder(vectorStore).build()
    private val simpleLoggerAdvisor = SimpleLoggerAdvisor()
    private val chatMemoryAdvisor = MessageChatMemoryAdvisor.builder(
        MessageWindowChatMemory.builder()
            .chatMemoryRepository(InMemoryChatMemoryRepository())
            .build()
    ).build()

    fun chat(chatId: String, question: String): String {
        return chatClient
            .prompt()
            .system { it.param("currentDate", LocalDate.now(clock)) }
            .user(question)
            .advisors(questionAnswerAdvisor, chatMemoryAdvisor, simpleLoggerAdvisor)
            .advisors { a -> a.param(ChatMemory.CONVERSATION_ID, chatId) }
            .call()
            .content().apply {
                logger.info("Chat #$chatId question: $question")
                logger.info("Chat #$chatId answer: $this")
            }!!
    }
}
