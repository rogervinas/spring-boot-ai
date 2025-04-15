package com.rogervinas.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.context.annotation.Profile
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset

@Configuration
@Profile("test")
class ClockTestToolConfiguration {

    @Primary
    @Bean
    fun testClock(): Clock = Clock.fixed(Instant.parse("2025-04-15T10:00:00Z"), ZoneOffset.systemDefault())
}