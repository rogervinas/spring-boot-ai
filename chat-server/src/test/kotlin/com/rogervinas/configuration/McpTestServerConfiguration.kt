package com.rogervinas.configuration

import io.modelcontextprotocol.server.McpServer
import io.modelcontextprotocol.server.McpSyncServer
import io.modelcontextprotocol.server.transport.WebFluxSseServerTransport
import org.springframework.ai.autoconfigure.mcp.server.McpServerProperties
import org.springframework.ai.mcp.McpToolUtils
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.LocalDate

@Configuration
@EnableConfigurationProperties(McpServerProperties::class)
class McpTestServerConfiguration {

    @Bean(destroyMethod = "close")
    fun mcpTestServer(bookingTestService: BookingTestService, transport: WebFluxSseServerTransport): McpSyncServer {
        val bookingTestToolRegistration = MethodToolCallbackProvider.builder()
            .toolObjects(BookingTestTool(bookingTestService))
            .build()
            .toolCallbacks.map { McpToolUtils.toSyncToolRegistration(it) }

        return McpServer
            .sync(transport)
            .tools(bookingTestToolRegistration)
            .build()
    }
}

class BookingTestTool(private val bookingTestService: BookingTestService) {
    @Tool(
        description = "make a reservation for accommodation for a given city and date",
    )
    fun book(
        @ToolParam(description = "the city to make the reservation for") city: String,
        @ToolParam(description = "the check-in date, when the guest begins their stay") checkinDate: LocalDate,
        @ToolParam(description = "the check-out date, when the guest ends their stay") checkoutDate: LocalDate
    ): String = bookingTestService.book(city, checkinDate, checkoutDate)
}

interface BookingTestService {
    fun book(city: String, checkinDate: LocalDate, checkoutDate: LocalDate): String
}
