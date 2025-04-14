package com.rogervinas.tools

import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.annotation.ToolParam
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import java.time.LocalDate

@Configuration
class WeatherToolConfiguration {
    @Bean
    fun weatherToolCallbackProvider(weatherTool: WeatherTool) = MethodToolCallbackProvider.builder()
        .toolObjects(weatherTool)
        .build()
}

@Service
class WeatherTool(private val weatherService: WeatherService) {
    @Tool(description = "get the weather for a given city and date")
    fun getWeather(
        @ToolParam(description = "the city to get the weather for") city: String,
        @ToolParam(description = "the date to get the weather for") date: LocalDate
    ): String = weatherService.getWeather(city, date)
}

@Service
class WeatherService {
    fun getWeather(city: String, date: LocalDate): String =
        "The weather in $city on $date is sunny with a high of 25°C"
}
