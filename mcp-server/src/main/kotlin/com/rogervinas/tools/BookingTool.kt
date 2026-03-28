package com.rogervinas.tools

import org.slf4j.LoggerFactory
import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import java.time.LocalDate

@Configuration
class BookingToolConfiguration {
    @Bean
    fun bookingToolCallbackProvider(bookingTool: BookingTool) = MethodToolCallbackProvider.builder()
        .toolObjects(bookingTool)
        .build()
}

@Service
class BookingTool(private val bookingService: BookingService) {
    // TODO use LocalDate directly when Spring AI fixes JsonParser.toTypedObject for bare string values
    //  see https://github.com/spring-projects/spring-ai/issues/5696
    @Tool(
        description = "make a reservation for accommodation for a given city and date",
    )
    fun book(
        @ToolParam(description = "the city to make the reservation for") city: String,
        @ToolParam(description = "the check-in date, when the reservation begins") checkinDate: String,
        @ToolParam(description = "the check-out date, when the reservation ends") checkoutDate: String
    ): String = bookingService.book(city, LocalDate.parse(checkinDate), LocalDate.parse(checkoutDate))
}

@Service
class BookingService {
    private val logger = LoggerFactory.getLogger(BookingService::class.java)

    fun book(city: String, checkinDate: LocalDate, checkoutDate: LocalDate): String {
        logger.info("Book accommodation in $city from $checkinDate to $checkoutDate")
        return "Your accommodation has been booked in $city from $checkinDate to $checkoutDate"
    }
}
