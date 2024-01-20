package com.lukinhasssss.catalogo.infrastructure.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.lukinhasssss.catalogo.infrastructure.configuration.json.Json
import org.springframework.boot.jackson.JsonComponent
import org.springframework.context.annotation.Bean

@JsonComponent
class ObjectMapperConfig {

    @Bean
    fun objectMapper(): ObjectMapper = Json.mapper().registerModule(kotlinModule())
}
