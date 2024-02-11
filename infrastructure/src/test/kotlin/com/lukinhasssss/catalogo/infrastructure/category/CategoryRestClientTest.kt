package com.lukinhasssss.catalogo.infrastructure.category

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.lukinhasssss.catalogo.IntegrationTestConfiguration
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.domain.exception.InternalErrorException
import com.lukinhasssss.catalogo.infrastructure.category.models.CategoryDTO
import com.lukinhasssss.catalogo.infrastructure.configuration.WebServerConfig
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import kotlin.test.assertEquals
import kotlin.test.assertNull

@Tag("integrationTest")
@ActiveProfiles("test-integration")
@AutoConfigureWireMock(port = 0)
@EnableAutoConfiguration(exclude = [ElasticsearchRepositoriesAutoConfiguration::class, KafkaAutoConfiguration::class])
@SpringBootTest(classes = [WebServerConfig::class, IntegrationTestConfiguration::class])
class CategoryRestClientTest {

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @Autowired
    lateinit var target: CategoryRestClient

    @Test
    fun givenACategory_whenReceive200FromServer_shouldBeOk() {
        // given
        val aulas = Fixture.Categories.aulas

        val responseBody = with(aulas) {
            objectMapper.writeValueAsString(CategoryDTO(id, name, description, isActive, createdAt, updatedAt, deletedAt))
        }

        stubFor(
            get(urlPathEqualTo("/api/categories/${aulas.id}"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(responseBody)
                )
        )

        // when
        val actualCategory = target.getById(aulas.id)

        // then
        with(actualCategory!!) {
            assertEquals(aulas.id, id)
            assertEquals(aulas.name, name)
            assertEquals(aulas.description, description)
            assertEquals(aulas.isActive, active)
            assertEquals(aulas.createdAt, createdAt)
            assertEquals(aulas.updatedAt, updatedAt)
            assertEquals(aulas.deletedAt, deletedAt)
        }
    }

    @Test
    fun givenACategory_whenReceive404NotFoundFromServer_shouldReturnNull() {
        // given
        val expectedId = "any"

        val responseBody = objectMapper.writeValueAsString(mapOf("message" to "Not found"))

        stubFor(
            get(urlPathEqualTo("/api/categories/$expectedId"))
                .willReturn(
                    aResponse()
                        .withStatus(404)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(responseBody)
                )
        )

        // when
        val actualException = target.getById(expectedId)

        // then
        assertNull(actualException)
    }

    @Test
    fun givenACategory_whenReceive5xxFromServer_shouldReturnInternalError() {
        // given
        val expectedId = "any"
        val expectedErrorMessage = "Failed to get Category of id $expectedId"

        val responseBody = objectMapper.writeValueAsString(mapOf("message" to "Internal Server Error"))

        stubFor(
            get(urlPathEqualTo("/api/categories/$expectedId"))
                .willReturn(
                    aResponse()
                        .withStatus(500)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(responseBody)
                )
        )

        // when
        val actualException = assertThrows<InternalErrorException> { target.getById(expectedId) }

        // then
        assertEquals(expectedErrorMessage, actualException.message)
    }

    @Test
    fun givenACategory_whenReceiveTimeout_shouldReturnInternalError() {
        // given
        val aulas = Fixture.Categories.aulas

        val expectedErrorMessage = "Timeout from category of ID ${aulas.id}"

        val responseBody = with(aulas) {
            objectMapper.writeValueAsString(CategoryDTO(id, name, description, isActive, createdAt, updatedAt, deletedAt))
        }

        stubFor(
            get(urlPathEqualTo("/api/categories/${aulas.id}"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withFixedDelay(600)
                        .withBody(responseBody)
                )
        )

        // when
        val actualException = assertThrows<InternalErrorException> { target.getById(aulas.id) }

        // then
        assertEquals(expectedErrorMessage, actualException.message)
    }
}
