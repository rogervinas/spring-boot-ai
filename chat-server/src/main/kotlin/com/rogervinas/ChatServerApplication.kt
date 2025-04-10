package com.rogervinas

import io.modelcontextprotocol.client.McpClient
import io.modelcontextprotocol.client.McpSyncClient
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor
import org.springframework.ai.chat.memory.InMemoryChatMemory
import org.springframework.ai.document.Document
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider
import org.springframework.ai.vectorstore.VectorStore
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.annotation.Id
import org.springframework.data.repository.ListCrudRepository
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import java.util.concurrent.ConcurrentHashMap

@SpringBootApplication
class ChatServerApplication

@Configuration
class ConversationalConfiguration {
    @Bean
    fun mcpClient(@Value("\${mcp-server.url}") url: String) = McpClient
        .sync(HttpClientSseClientTransport(url))
        .build().apply {
            initialize()
        }

    @Bean
    fun chatClient(
        mcpSyncClient: McpSyncClient,
        builder: ChatClient.Builder
    ): ChatClient {
        val system = """
                You are an AI powered assistant to help people adopt a dog from the adoption 
                agency named Pooch Palace with locations in Atlanta, Antwerp, Seoul, Tokyo, Singapore, Paris, 
                Mumbai, New Delhi, Barcelona, San Francisco, and London. Information about the dogs available 
                will be presented below. If there is no information, then return a polite response suggesting we 
                don't have any dogs available.
                
                If the response involves a timestamp, be sure to convert it to something human-readable.
                
                Do _not_ include any indication of what you're thinking. Nothing should be sent to the client between <thinking> tags. 
                Just give the answer.
                
                """.trimIndent()
        return builder
            .defaultSystem(system)
            .defaultTools(SyncMcpToolCallbackProvider(mcpSyncClient))
            .build()
    }
}

interface DogRepository : ListCrudRepository<Dog, Int>

data class Dog(@Id val id: Int, val name: String, val owner: String?, val description: String)

@Controller
@ResponseBody
class ConversationalController(vectorStore: VectorStore, private val chatClient: ChatClient) {
    private val questionAnswerAdvisor = QuestionAnswerAdvisor(vectorStore)
    private val chatMemory = ConcurrentHashMap<String, PromptChatMemoryAdvisor>()

    @PostMapping("/{id}/inquire")
    fun inquire(@PathVariable id: String, @RequestParam question: String): String? {
        val promptChatMemoryAdvisor = chatMemory
            .computeIfAbsent(id) { _: String -> PromptChatMemoryAdvisor.builder(InMemoryChatMemory()).build() }
        return chatClient
            .prompt()
            .user(question)
            .advisors(questionAnswerAdvisor, promptChatMemoryAdvisor)
            .call()
            .content()
    }
}

@Configuration
class DogDataInitializerConfiguration {

    @Bean
    fun initializerRunner(vectorStore: VectorStore, dogRepository: DogRepository): ApplicationRunner {
        return ApplicationRunner {
            dogRepository.deleteAll()
            if (dogRepository.count() == 0L) {
                println("initializing vector store");
                var map = mapOf(
                    "Jasper" to "A grey Shih Tzu known for being protective.",
                    "Toby" to "A grey Doberman known for being playful.",
                    "Nala" to "A spotted German Shepherd known for being loyal.",
                    "Penny" to "A white Great Dane known for being protective.",
                    "Bella" to "A golden Poodle known for being calm.",
                    "Willow" to "A brindle Great Dane known for being calm.",
                    "Daisy" to "A spotted Poodle known for being affectionate.",
                    "Mia" to "A grey Great Dane known for being loyal.",
                    "Molly" to "A golden Chihuahua known for being curious.",
                    "Prancer" to "A demonic, neurotic, man hating, animal hating, children hating dogs that look like gremlins."
                )
                map.forEach { name, description ->
                    var dog = dogRepository.save(Dog(0, name, null, description));
                    var dogument = Document("id: ${dog.id}, name: ${dog.name}, description: ${dog.description}")
                    vectorStore.add(listOf(dogument));
                }
                println("finished initializing vector store")
            }
        };
    }
}

fun main(args: Array<String>) {
    runApplication<ChatServerApplication>(*args)
}
