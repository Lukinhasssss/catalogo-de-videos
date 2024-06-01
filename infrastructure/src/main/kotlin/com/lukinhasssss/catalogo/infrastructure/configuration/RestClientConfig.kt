package com.lukinhasssss.catalogo.infrastructure.configuration

import com.fasterxml.jackson.databind.ObjectMapper
import com.lukinhasssss.catalogo.infrastructure.configuration.annotations.Categories
import com.lukinhasssss.catalogo.infrastructure.configuration.annotations.Genres
import com.lukinhasssss.catalogo.infrastructure.configuration.annotations.Keycloak
import com.lukinhasssss.catalogo.infrastructure.configuration.annotations.Videos
import com.lukinhasssss.catalogo.infrastructure.configuration.properties.RestClientProperties
import io.micrometer.observation.ObservationRegistry
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.http.converter.FormHttpMessageConverter
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.web.client.RestClient

@Configuration(proxyBeanMethods = false)
class RestClientConfig {

    companion object {
        fun restClient(
            properties: RestClientProperties,
            objectMapper: ObjectMapper,
            observationRegistry: ObservationRegistry
        ) = with(properties) {
            val factory = JdkClientHttpRequestFactory()
            factory.setReadTimeout(readTimeout)

            RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .messageConverters { converters ->
                    converters.removeIf { it is MappingJackson2HttpMessageConverter }
                    converters.add(jsonConverter(objectMapper))
                    converters.add(FormHttpMessageConverter())
                }
                .observationRegistry(observationRegistry)
                .observationConvention(RestClientObservationConfig())
                .build()
        }

        private fun jsonConverter(objectMapper: ObjectMapper): MappingJackson2HttpMessageConverter {
            val jsonConverter = MappingJackson2HttpMessageConverter(objectMapper)
            jsonConverter.supportedMediaTypes = listOf(MediaType.APPLICATION_JSON)
            return jsonConverter
        }
    }

    @Bean
    @ConfigurationProperties(prefix = "rest-client.keycloak")
    @Keycloak
    fun keycloakRestClientProperties() = RestClientProperties()

    @Bean
    @ConfigurationProperties(prefix = "rest-client.categories")
    @Categories
    fun categoryRestClientProperties() = RestClientProperties()

    @Bean
    @ConfigurationProperties(prefix = "rest-client.genres")
    @Genres
    fun genreRestClientProperties() = RestClientProperties()

    @Bean
    @ConfigurationProperties(prefix = "rest-client.videos")
    @Videos
    fun videoRestClientProperties() = RestClientProperties()

    @Bean
    @Keycloak
    fun keycloakHttpClient(
        @Keycloak properties: RestClientProperties,
        objectMapper: ObjectMapper,
        observationRegistry: ObservationRegistry
    ) = restClient(properties, objectMapper, observationRegistry)

    @Bean
    @Categories
    fun categoryHttpClient(
        @Categories properties: RestClientProperties,
        objectMapper: ObjectMapper,
        observationRegistry: ObservationRegistry
    ) = restClient(properties, objectMapper, observationRegistry)

    @Bean
    @Genres
    fun genreHttpClient(
        @Genres properties: RestClientProperties,
        objectMapper: ObjectMapper,
        observationRegistry: ObservationRegistry
    ) = restClient(properties, objectMapper, observationRegistry)

    @Bean
    @Videos
    fun videoHttpClient(
        @Videos properties: RestClientProperties,
        objectMapper: ObjectMapper,
        observationRegistry: ObservationRegistry
    ) = restClient(properties, objectMapper, observationRegistry)
}
