package com.rogervinas

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.whenever
import com.rogervinas.tools.BookingService
import io.modelcontextprotocol.client.McpClient
import io.modelcontextprotocol.client.McpSyncClient
import io.modelcontextprotocol.client.transport.HttpClientSseClientTransport
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest
import io.modelcontextprotocol.spec.McpSchema.TextContent
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS
import org.junit.jupiter.api.TestMethodOrder
import org.mockito.Mockito.doReturn
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.context.bean.override.mockito.MockitoBean
import java.time.LocalDate
import java.util.function.Consumer


@SpringBootTest(webEnvironment = RANDOM_PORT)
@TestInstance(PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class McpServerApplicationTest {

    @LocalServerPort
    val port: Int = 0

    val client: McpSyncClient by lazy {
        McpClient.sync(HttpClientSseClientTransport.builder("http://localhost:$port").build()).build().apply {
            initialize()
            ping()
        }
    }

    @AfterAll
    fun closeClient() {
        client.close()
    }

    @MockitoBean
    lateinit var bookingService: BookingService

    @Test
    @Order(0)
    fun `should list tools`() {
        assertThat(client.listTools().tools).singleElement().satisfies(Consumer {
            assertThat(it.name).isEqualTo("book")
            assertThat(it.description).isEqualTo("make a reservation for accommodation for a given city and date")
        })
    }

    @Test
    fun `should book`() {
        val bookResult = "Booking is done!"
        val cityCaptor = argumentCaptor<String>()
        val checkinDateCaptor = argumentCaptor<LocalDate>()
        val checkoutDateCaptor = argumentCaptor<LocalDate>()
        doReturn(bookResult)
            .whenever(bookingService)
            .book(cityCaptor.capture(), checkinDateCaptor.capture(), checkoutDateCaptor.capture())

        val city = "Barcelona"
        val checkinDate = "2025-04-15"
        val checkoutDate = "2025-04-18"
        val result = client.callTool(
            CallToolRequest(
                "book",
                mapOf(
                    "city" to city,
                    "checkinDate" to checkinDate,
                    "checkoutDate" to checkoutDate
                )
            )
        )

        assertThat(result.isError).isFalse()
        assertThat(result.content).singleElement().isInstanceOfSatisfying(TextContent::class.java) {
            // TODO why is text double quoted?
            assertThat(it.text).isEqualTo("\"$bookResult\"")
        }
        assertThat(cityCaptor.allValues).singleElement().isEqualTo(city)
        assertThat(checkinDateCaptor.allValues).singleElement().isEqualTo(LocalDate.parse(checkinDate))
        assertThat(checkoutDateCaptor.allValues).singleElement().isEqualTo(LocalDate.parse(checkoutDate))
    }
}
