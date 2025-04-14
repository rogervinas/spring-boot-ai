package com.rogervinas.tools

import io.modelcontextprotocol.client.McpClient
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("!test")
class BookingToolConfiguration {
    @Bean
    fun bookingToolCallbackProvider(@Value("\${booking-server.url}") url: String) =
        SyncMcpToolCallbackProvider(mcpSyncClient(url))

    private fun mcpSyncClient(url: String) = McpClient
        .sync(HttpClientSseClientTransport(url))
        .build().apply {
            initialize()
        }
}
