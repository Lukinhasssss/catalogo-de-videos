package com.lukinhasssss.catalogo

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import com.lukinhasssss.catalogo.infrastructure.category.CategoryRestClient
import com.lukinhasssss.catalogo.infrastructure.configuration.WebServerConfig
import io.github.resilience4j.bulkhead.BulkheadRegistry
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

    companion object {
        const val CATEGORY = CategoryRestClient.NAMESPACE
    }

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    private lateinit var bulkheadRegistry: BulkheadRegistry

    @BeforeEach
    fun before() {
        WireMock.reset()
        WireMock.resetAllRequests()
        // listOf(CATEGORY).forEach { resetFaultTolerance(it) }
    }

    fun acquireBulkheadPermission(name: String) =
        bulkheadRegistry.bulkhead(name).acquirePermission()

    fun releaseBulkheadPermission(name: String) =
        bulkheadRegistry.bulkhead(name).releasePermission()

    fun writeValueAsString(obj: Any): String = objectMapper.writeValueAsString(obj)

    // private fun resetFaultTolerance(name: String) = {}
}
