package com.lukinhasssss.catalogo.infrastructure.configuration

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.cache.Cache2kBuilderCustomizer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit

@Configuration(proxyBeanMethods = false)
@EnableCaching
class CacheConfig {

    @Bean
    fun cache2kBuilderCustomizer(
        @Value("\${cache.max-entries}") maxEntries: Long,
        @Value("\${cache.ttl}") ttl: Long
    ) = Cache2kBuilderCustomizer {
        it.entryCapacity(maxEntries).expireAfterWrite(ttl, TimeUnit.SECONDS)
    }
}
