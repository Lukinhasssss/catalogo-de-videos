package com.lukinhasssss.catalogo.infrastructure.category

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.verify
import com.lukinhasssss.catalogo.AbstractRestClientTest
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.domain.Fixture.Categories.aulas
import com.lukinhasssss.catalogo.domain.exception.InternalErrorException
import com.lukinhasssss.catalogo.infrastructure.category.models.CategoryDTO
import io.github.resilience4j.bulkhead.BulkheadFullException
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CategoryRestClientTest : AbstractRestClientTest() {

    @Autowired
    lateinit var target: CategoryRestClient

    @Test
    fun givenACategory_whenReceive200FromServer_shouldBeOk() {
        // given
        val aulas = Fixture.Categories.aulas

        val responseBody = with(aulas) {
            writeValueAsString(CategoryDTO(id, name, description, isActive, createdAt, updatedAt, deletedAt))
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

        val responseBody = writeValueAsString(mapOf("message" to "Not found"))

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
        val actualCategory = target.getById(expectedId)

        // then
        assertNull(actualCategory)

        verify(1, getRequestedFor(urlPathEqualTo("/api/categories/$expectedId")))
    }

    @Test
    fun givenACategory_whenReceive5xxFromServer_shouldReturnInternalError() {
        // given
        val expectedId = "any"
        val expectedErrorMessage = "Error observed from categories [resourceId: $expectedId] [status: 500]"

        val responseBody = writeValueAsString(mapOf("message" to "Internal Server Error"))

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

        verify(2, getRequestedFor(urlPathEqualTo("/api/categories/$expectedId")))
    }

    @Test
    fun givenACategory_whenReceiveTimeout_shouldReturnInternalError() {
        // given
        val aulas = Fixture.Categories.aulas

        val expectedErrorMessage = "Timeout observed from categories [resourceId: ${aulas.id}]"

        val responseBody = with(aulas) {
            writeValueAsString(CategoryDTO(id, name, description, isActive, createdAt, updatedAt, deletedAt))
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

        verify(2, getRequestedFor(urlPathEqualTo("/api/categories/${aulas.id}")))
    }

    @Test
    fun givenACategory_whenBulkheadIsFull_shouldReturnError() {
        // given
        val expectedErrorMessage = "Bulkhead 'categories' is full and does not permit further calls"

        acquireBulkheadPermission(CATEGORY)

        // when
        val actualException = assertThrows<BulkheadFullException> { target.getById(aulas.id) }

        // then
        assertEquals(expectedErrorMessage, actualException.message)

        releaseBulkheadPermission(CATEGORY)
    }

    @Test
    fun givenServerError_whenIsMoreThanThreshold_shouldOpenCircuitBreaker() {
        // given
        val expectedId = "any"
        val expectedErrorMessage = "CircuitBreaker 'categories' is OPEN and does not permit further calls"

        val responseBody = writeValueAsString(mapOf("message" to "Internal Server Error"))

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
        assertThrows<InternalErrorException> { target.getById(expectedId) }
        val actualException = assertThrows<CallNotPermittedException> { target.getById(expectedId) }

        // then
        checkCircuitBreakerState(CATEGORY, CircuitBreaker.State.OPEN)

        assertEquals(expectedErrorMessage, actualException.message)

        verify(3, getRequestedFor(urlPathEqualTo("/api/categories/$expectedId")))
    }

    @Test
    fun givenCall_whenCircuitBreakerIsOpen_shouldReturnError() {
        // given
        transitionToOpenState(CATEGORY)

        val expectedId = "any"
        val expectedErrorMessage = "CircuitBreaker 'categories' is OPEN and does not permit further calls"

        // when
        val actualException = assertThrows<CallNotPermittedException> { target.getById(expectedId) }

        // then
        checkCircuitBreakerState(CATEGORY, CircuitBreaker.State.OPEN)

        assertEquals(expectedErrorMessage, actualException.message)

        verify(0, getRequestedFor(urlPathEqualTo("/api/categories/$expectedId")))
    }
}
