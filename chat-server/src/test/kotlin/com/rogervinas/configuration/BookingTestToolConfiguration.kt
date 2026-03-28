package com.rogervinas.configuration

import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import java.time.LocalDate

@Configuration
class BookingTestToolConfiguration {

    @Bean
    fun bookingToolCallbackProvider(bookingTestTool: BookingTestTool) = MethodToolCallbackProvider.builder()
        .toolObjects(bookingTestTool)
        .build()
}

@Service
class BookingTestTool(private val bookingTestService: BookingTestService) {
    // TODO use LocalDate directly when Spring AI fixes JsonParser.toTypedObject for bare string values
    //  see https://github.com/spring-projects/spring-ai/issues/5696
    @Tool(
        description = "make a reservation for accommodation for a given city and date",
    )
    fun book(
        @ToolParam(description = "the city to make the reservation for") city: String,
        @ToolParam(description = "the check-in date, when the reservation begins") checkinDate: String,
        @ToolParam(description = "the check-out date, when the reservation ends") checkoutDate: String
    ): String = bookingTestService.book(city, LocalDate.parse(checkinDate), LocalDate.parse(checkoutDate))
}

interface BookingTestService {
    fun book(city: String, checkinDate: LocalDate, checkoutDate: LocalDate): String
}
