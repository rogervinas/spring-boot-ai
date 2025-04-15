package com.rogervinas.chat

import org.slf4j.LoggerFactory
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor
import org.springframework.ai.chat.memory.InMemoryChatMemory
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class ChatService(vectorStore: VectorStore, private val chatClient: ChatClient) {

    private val logger = LoggerFactory.getLogger(ChatService::class.java)
    private val questionAnswerAdvisor = QuestionAnswerAdvisor(vectorStore)
    private val simpleLoggerAdvisor = SimpleLoggerAdvisor()
    private val chatMemory = ConcurrentHashMap<String, PromptChatMemoryAdvisor>()

    fun chat(chatId: String, question: String): String? {
        val chatMemoryAdvisor = chatMemory.computeIfAbsent(chatId) {
            PromptChatMemoryAdvisor.builder(InMemoryChatMemory()).build()
        }
        return chatClient
            .prompt()
            .user(question)
            .advisors(questionAnswerAdvisor, chatMemoryAdvisor, simpleLoggerAdvisor)
            .call()
            .content().apply {
                logger.info("Chat #$chatId question: $question")
                logger.info("Chat #$chatId answer: $this")
            }
    }
}

