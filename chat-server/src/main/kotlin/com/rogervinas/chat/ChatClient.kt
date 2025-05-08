package com.rogervinas.chat

import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.tool.ToolCallbackProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class ChatClientConfiguration {
    @Bean
    fun chatClient(
        builder: ChatClient.Builder,
        toolCallbackProviders: List<ToolCallbackProvider>
    ): ChatClient {
        return chatClientBuilder(builder, toolCallbackProviders).build()
    }

    private fun chatClientBuilder(
        builder: ChatClient.Builder,
        toolCallbackProviders: List<ToolCallbackProvider>
    ): ChatClient.Builder {
        val system = """
        You are an AI powered assistant to help people book accommodation in touristic cities around the world.
        If there is no information, then return a polite response suggesting you don't know.
        If the response involves a timestamp, be sure to convert it to something human-readable.
        Do not include any indication of what you're thinking.
        Use the tools available to you to answer the questions.
        Just give the answer.
        """.trimIndent()
        return builder
            .defaultSystem(system)
            .defaultToolCallbacks(*toolCallbackProviders.toTypedArray())
    }
}
