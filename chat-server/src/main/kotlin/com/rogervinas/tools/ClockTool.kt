package com.rogervinas.tools

import org.springframework.ai.tool.annotation.Tool
import org.springframework.ai.tool.method.MethodToolCallbackProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Service
import java.time.Clock
import java.time.LocalDate

@Configuration
class ClockToolConfiguration {
    @Bean
    fun clockToolCallbackProvider(clocktTool: ClockTool) = MethodToolCallbackProvider.builder()
        .toolObjects(clocktTool)
        .build()

    @Bean
    fun clock(): Clock = Clock.systemDefaultZone()
}

@Service
class ClockTool(private val clock: Clock) {
    @Tool(description = "get the current date")
    fun getDate(): LocalDate = LocalDate.now(clock)
}
