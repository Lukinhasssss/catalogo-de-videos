package com.lukinhasssss.catalogo.infrastructure.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "kafka")
data class KafkaProperties(
    var bootstrapServers: String = String(),
    var poolTimeout: Long = 0L,
    var autoCreateTopics: Boolean = false
)
