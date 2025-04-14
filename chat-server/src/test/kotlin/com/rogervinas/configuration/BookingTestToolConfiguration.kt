package com.rogervinas.configuration

import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import java.time.LocalDate

@Configuration
@Profile("test")
class BookingTestToolConfiguration {

    @Bean
    fun bookingToolCallbackProvider(bookingTestTool: BookingTestTool) = MethodToolCallbackProvider.builder()
        .toolObjects(bookingTestTool)
        .build()
}

@Service
@Profile("test")
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

@Service
@Profile("test")
class BookingTestService {
    fun book(city: String, checkinDate: LocalDate, checkoutDate: LocalDate): String =
        "Your accommodation has been booked in $city from $checkinDate to $checkoutDate"
}
