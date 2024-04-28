package com.lukinhasssss.catalogo.infrastructure.genre

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.verify
import com.lukinhasssss.catalogo.AbstractRestClientTest
import com.lukinhasssss.catalogo.domain.Fixture.Genres.business
import com.lukinhasssss.catalogo.domain.exception.InternalErrorException
import com.lukinhasssss.catalogo.infrastructure.authentication.ClientCredentialsManager
import com.lukinhasssss.catalogo.infrastructure.genre.models.GenreDTO
import com.ninjasquad.springmockk.SpykBean
import io.github.resilience4j.bulkhead.BulkheadFullException
import io.github.resilience4j.circuitbreaker.CallNotPermittedException
import io.github.resilience4j.circuitbreaker.CircuitBreaker
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GenreRestGatewayTest : AbstractRestClientTest() {

    @Autowired
    lateinit var target: GenreRestGateway

    @SpykBean
    lateinit var credentialsManager: ClientCredentialsManager

    @BeforeEach
    fun setCredentialsManager() { every { credentialsManager.retrieve() } returns "token" }

    @Test
    fun givenAGenre_whenReceive200FromServer_shouldBeOk() {
        // given
        val business = business()

        val responseBody = with(business) {
            writeValueAsString(GenreDTO(id, name, isActive, categories, createdAt, updatedAt, deletedAt))
        }

        stubFor(
            get(urlPathEqualTo("/api/genres/${business.id}"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(responseBody)
                )
        )

        // when
        val actualGenre = target.genreOfId(business.id)

        // then
        with(actualGenre!!) {
            assertEquals(business.id, id)
            assertEquals(business.name, name)
            assertEquals(business.isActive, isActive)
            assertEquals(business.categories, categoriesId)
            assertEquals(business.createdAt, createdAt)
            assertEquals(business.updatedAt, updatedAt)
            assertEquals(business.deletedAt, deletedAt)
        }

        verify(1, getRequestedFor(urlPathEqualTo("/api/genres/${business.id}")))
    }

    @Test
    fun givenAGenre_whenReceiveTwoCalls_shouldReturnCachedValue() {
        // given
        val business = business()

        val responseBody = with(business) {
            writeValueAsString(GenreDTO(id, name, isActive, categories, createdAt, updatedAt, deletedAt))
        }

        stubFor(
            get(urlPathEqualTo("/api/genres/${business.id}"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(responseBody)
                )
        )

        // when
        target.genreOfId(business.id)
        target.genreOfId(business.id)
        val actualGenre = target.genreOfId(business.id)

        // then
        with(actualGenre!!) {
            assertEquals(business.id, id)
            assertEquals(business.name, name)
            assertEquals(business.isActive, isActive)
            assertEquals(business.categories, categoriesId)
            assertEquals(business.createdAt, createdAt)
            assertEquals(business.updatedAt, updatedAt)
            assertEquals(business.deletedAt, deletedAt)
        }

        val actualCachedValue = cache("admin-genres")?.get(business.id)?.get()
        assertEquals(actualGenre, actualCachedValue)

        verify(1, getRequestedFor(urlPathEqualTo("/api/genres/${business.id}")))
    }

    @Test
    fun givenAGenre_whenReceive404NotFoundFromServer_shouldReturnNull() {
        // given
        val expectedId = "any"

        val responseBody = writeValueAsString(mapOf("message" to "Not found"))

        stubFor(
            get(urlPathEqualTo("/api/genres/$expectedId"))
                .willReturn(
                    aResponse()
                        .withStatus(404)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(responseBody)
                )
        )

        // when
        val actualGenre = target.genreOfId(expectedId)

        // then
        assertNull(actualGenre)

        verify(1, getRequestedFor(urlPathEqualTo("/api/genres/$expectedId")))
    }

    @Test
    fun givenAGenre_whenReceive5xxFromServer_shouldReturnInternalError() {
        // given
        val expectedId = "any"
        val expectedErrorMessage = "Error observed from genres [resourceId: $expectedId] [status: 500]"

        val responseBody = writeValueAsString(mapOf("message" to "Internal Server Error"))

        stubFor(
            get(urlPathEqualTo("/api/genres/$expectedId"))
                .willReturn(
                    aResponse()
                        .withStatus(500)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(responseBody)
                )
        )

        // when
        val actualException = assertThrows<InternalErrorException> { target.genreOfId(expectedId) }

        // then
        assertEquals(expectedErrorMessage, actualException.message)

        verify(2, getRequestedFor(urlPathEqualTo("/api/genres/$expectedId")))
    }

    @Test
    fun givenAGenre_whenReceiveTimeout_shouldReturnInternalError() {
        // given
        val business = business()

        val expectedErrorMessage = "Timeout observed from genres [resourceId: ${business.id}]"

        val responseBody = with(business) {
            writeValueAsString(GenreDTO(id, name, isActive, categories, createdAt, updatedAt, deletedAt))
        }

        stubFor(
            get(urlPathEqualTo("/api/genres/${business.id}"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withFixedDelay(600)
                        .withBody(responseBody)
                )
        )

        // when
        val actualException = assertThrows<InternalErrorException> { target.genreOfId(business.id) }

        // then
        assertEquals(expectedErrorMessage, actualException.message)

        verify(2, getRequestedFor(urlPathEqualTo("/api/genres/${business.id}")))
    }

    @Test
    fun givenAGenre_whenBulkheadIsFull_shouldReturnError() {
        // given
        val business = business()

        val expectedErrorMessage = "Bulkhead 'genres' is full and does not permit further calls"

        acquireBulkheadPermission(GENRE)

        // when
        val actualException = assertThrows<BulkheadFullException> { target.genreOfId(business.id) }

        // then
        assertEquals(expectedErrorMessage, actualException.message)

        releaseBulkheadPermission(GENRE)
    }

    @Test
    fun givenServerError_whenIsMoreThanThreshold_shouldOpenCircuitBreaker() {
        // given
        val expectedId = "any"
        val expectedErrorMessage = "CircuitBreaker 'genres' is OPEN and does not permit further calls"

        val responseBody = writeValueAsString(mapOf("message" to "Internal Server Error"))

        stubFor(
            get(urlPathEqualTo("/api/genres/$expectedId"))
                .willReturn(
                    aResponse()
                        .withStatus(500)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(responseBody)
                )
        )

        // when
        assertThrows<InternalErrorException> { target.genreOfId(expectedId) }
        val actualException = assertThrows<CallNotPermittedException> { target.genreOfId(expectedId) }

        // then
        checkCircuitBreakerState(GENRE, CircuitBreaker.State.OPEN)

        assertEquals(expectedErrorMessage, actualException.message)

        verify(3, getRequestedFor(urlPathEqualTo("/api/genres/$expectedId")))
    }

    @Test
    fun givenCall_whenCircuitBreakerIsOpen_shouldReturnError() {
        // given
        transitionToOpenState(GENRE)

        val expectedId = "any"
        val expectedErrorMessage = "CircuitBreaker 'genres' is OPEN and does not permit further calls"

        // when
        val actualException = assertThrows<CallNotPermittedException> { target.genreOfId(expectedId) }

        // then
        checkCircuitBreakerState(GENRE, CircuitBreaker.State.OPEN)

        assertEquals(expectedErrorMessage, actualException.message)

        verify(0, getRequestedFor(urlPathEqualTo("/api/genres/$expectedId")))
    }
}
