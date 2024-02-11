package com.lukinhasssss.catalogo.infrastructure.configuration

import com.lukinhasssss.catalogo.infrastructure.configuration.properties.RestClientProperties
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.JdkClientHttpRequestFactory
import org.springframework.web.client.RestClient

@Configuration
class RestClientConfig {

    @Bean
    @ConfigurationProperties(prefix = "rest-client.categories")
    fun categoryRestClientProperties() = RestClientProperties()

    @Bean
    fun categoryHttpClient(properties: RestClientProperties): RestClient = with(properties) {
        val factory = JdkClientHttpRequestFactory()
        factory.setReadTimeout(readTimeout)

        RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build()
    }
}
