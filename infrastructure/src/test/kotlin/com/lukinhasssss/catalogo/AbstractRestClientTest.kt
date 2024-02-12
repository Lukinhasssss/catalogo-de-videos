package com.lukinhasssss.catalogo

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock
import com.lukinhasssss.catalogo.infrastructure.category.CategoryRestClient
import com.lukinhasssss.catalogo.infrastructure.configuration.WebServerConfig
import io.github.resilience4j.bulkhead.BulkheadRegistry
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals

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

    @Autowired
    private lateinit var circuitBreakerRegistry: CircuitBreakerRegistry

    @BeforeEach
    fun before() {
        WireMock.reset()
        WireMock.resetAllRequests()
        listOf(CATEGORY).forEach { resetFaultTolerance(it) }
    }

    fun acquireBulkheadPermission(name: String) = bulkheadRegistry.bulkhead(name).acquirePermission()

    fun releaseBulkheadPermission(name: String) = bulkheadRegistry.bulkhead(name).releasePermission()

    fun checkCircuitBreakerState(name: String, expectedState: CircuitBreaker.State) =
        circuitBreakerRegistry.circuitBreaker(name).run { assertEquals(expectedState, state) }

    fun transitionToOpenState(name: String) = circuitBreakerRegistry.circuitBreaker(name).transitionToOpenState()

    fun transitionToClosedState(name: String) = circuitBreakerRegistry.circuitBreaker(name).transitionToClosedState()

    fun writeValueAsString(obj: Any): String = objectMapper.writeValueAsString(obj)

    private fun resetFaultTolerance(name: String) = circuitBreakerRegistry.circuitBreaker(name).reset()
}
