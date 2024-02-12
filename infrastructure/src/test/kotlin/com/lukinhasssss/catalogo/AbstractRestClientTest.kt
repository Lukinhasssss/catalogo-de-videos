package com.lukinhasssss.catalogo

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import com.lukinhasssss.catalogo.infrastructure.configuration.WebServerConfig
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.test.context.ActiveProfiles

@Tag("integrationTest")
@ActiveProfiles("test-integration")
@AutoConfigureWireMock(port = 0)
@EnableAutoConfiguration(exclude = [ElasticsearchRepositoriesAutoConfiguration::class, KafkaAutoConfiguration::class])
@SpringBootTest(classes = [WebServerConfig::class, IntegrationTestConfiguration::class])
abstract class AbstractRestClientTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @BeforeEach
    fun before() {
        WireMock.reset()
        WireMock.resetAllRequests()
    }

    fun writeValueAsString(obj: Any): String = objectMapper.writeValueAsString(obj)
}
