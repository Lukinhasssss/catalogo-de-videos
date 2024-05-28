package com.lukinhasssss.catalogo.infrastructure.video

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.verify
import com.lukinhasssss.catalogo.AbstractRestClientTest
import com.lukinhasssss.catalogo.domain.Fixture.Videos.cleanCode
import com.lukinhasssss.catalogo.domain.exception.InternalErrorException
import com.lukinhasssss.catalogo.domain.utils.IdUtils
import com.lukinhasssss.catalogo.infrastructure.authentication.ClientCredentialsManager
import com.lukinhasssss.catalogo.infrastructure.video.models.ImageResourceDTO
import com.lukinhasssss.catalogo.infrastructure.video.models.VideoDTO
import com.lukinhasssss.catalogo.infrastructure.video.models.VideoResourceDTO
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

class VideoRestClientTest : AbstractRestClientTest() {

    @Autowired
    lateinit var target: VideoRestClient

    @SpykBean
    lateinit var credentialsManager: ClientCredentialsManager

    @BeforeEach
    fun setCredentialsManager() { every { credentialsManager.retrieve() } returns "token" }

    @Test
    fun givenAVideo_whenReceive200FromServer_shouldBeOk() {
        // given
        val cleanCode = cleanCode()

        val responseBody = with(cleanCode) {
            writeValueAsString(
                VideoDTO(
                    id = id,
                    title = title,
                    description = description,
                    yearLaunched = launchedAt.value,
                    rating = rating.name,
                    duration = duration,
                    opened = opened,
                    published = published,
                    banner = imageResourceDTO(banner!!),
                    thumbnail = imageResourceDTO(thumbnail!!),
                    thumbnailHalf = imageResourceDTO(thumbnailHalf!!),
                    trailer = videoResourceDTO(trailer!!),
                    video = videoResourceDTO(this.video!!),
                    categoriesId = categories,
                    castMembersId = castMembers,
                    genresId = genres,
                    createdAt = createdAt.toString(),
                    updatedAt = updatedAt.toString()
                )
            )
        }

        stubFor(
            get(urlPathEqualTo("/api/videos/${cleanCode.id}"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(responseBody)
                )
        )

        // when
        val actualVideo = target.videoOfId(cleanCode.id)

        // then
        with(actualVideo!!) {
            assertEquals(cleanCode.id, id)
            assertEquals(cleanCode.title, title)
            assertEquals(cleanCode.description, description)
            assertEquals(cleanCode.launchedAt.value, yearLaunched)
            assertEquals(cleanCode.rating.name, rating)
            assertEquals(cleanCode.duration, duration)
            assertEquals(cleanCode.opened, opened)
            assertEquals(cleanCode.published, published)
            assertEquals(cleanCode.banner, banner?.location)
            assertEquals(cleanCode.thumbnail, thumbnail?.location)
            assertEquals(cleanCode.thumbnailHalf, thumbnailHalf?.location)
            assertEquals(cleanCode.trailer, trailer?.encodedLocation)
            assertEquals(cleanCode.video, video?.encodedLocation)
            assertEquals(cleanCode.categories, categoriesId)
            assertEquals(cleanCode.castMembers, castMembersId)
            assertEquals(cleanCode.genres, genresId)
            assertEquals(cleanCode.createdAt.toString(), createdAt)
            assertEquals(cleanCode.updatedAt.toString(), updatedAt)
        }

        verify(1, getRequestedFor(urlPathEqualTo("/api/videos/${cleanCode.id}")))
    }

    @Test
    fun givenAVideo_whenReceiveTwoCalls_shouldReturnCachedValue() {
        // given
        val cleanCode = cleanCode()

        val responseBody = with(cleanCode) {
            writeValueAsString(
                VideoDTO(
                    id = id,
                    title = title,
                    description = description,
                    yearLaunched = launchedAt.value,
                    rating = rating.name,
                    duration = duration,
                    opened = opened,
                    published = published,
                    banner = imageResourceDTO(banner!!),
                    thumbnail = imageResourceDTO(thumbnail!!),
                    thumbnailHalf = imageResourceDTO(thumbnailHalf!!),
                    trailer = videoResourceDTO(trailer!!),
                    video = videoResourceDTO(this.video!!),
                    categoriesId = categories,
                    castMembersId = castMembers,
                    genresId = genres,
                    createdAt = createdAt.toString(),
                    updatedAt = updatedAt.toString()
                )
            )
        }

        stubFor(
            get(urlPathEqualTo("/api/videos/${cleanCode.id}"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(responseBody)
                )
        )

        // when
        target.videoOfId(cleanCode.id)
        target.videoOfId(cleanCode.id)
        val actualVideo = target.videoOfId(cleanCode.id)

        // then
        with(actualVideo!!) {
            assertEquals(cleanCode.id, id)
            assertEquals(cleanCode.title, title)
            assertEquals(cleanCode.description, description)
            assertEquals(cleanCode.launchedAt.value, yearLaunched)
            assertEquals(cleanCode.rating.name, rating)
            assertEquals(cleanCode.duration, duration)
            assertEquals(cleanCode.opened, opened)
            assertEquals(cleanCode.published, published)
            assertEquals(cleanCode.banner, banner?.location)
            assertEquals(cleanCode.thumbnail, thumbnail?.location)
            assertEquals(cleanCode.thumbnailHalf, thumbnailHalf?.location)
            assertEquals(cleanCode.trailer, trailer?.encodedLocation)
            assertEquals(cleanCode.video, video?.encodedLocation)
            assertEquals(cleanCode.categories, categoriesId)
            assertEquals(cleanCode.castMembers, castMembersId)
            assertEquals(cleanCode.genres, genresId)
            assertEquals(cleanCode.createdAt.toString(), createdAt)
            assertEquals(cleanCode.updatedAt.toString(), updatedAt)
        }

        val actualCachedValue = cache("admin-videos")?.get(cleanCode.id)?.get()
        assertEquals(actualVideo, actualCachedValue)

        verify(1, getRequestedFor(urlPathEqualTo("/api/videos/${cleanCode.id}")))
    }

    @Test
    fun givenAVideo_whenReceive404NotFoundFromServer_shouldReturnNull() {
        // given
        val expectedId = "any"

        val responseBody = writeValueAsString(mapOf("message" to "Not found"))

        stubFor(
            get(urlPathEqualTo("/api/videos/$expectedId"))
                .willReturn(
                    aResponse()
                        .withStatus(404)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(responseBody)
                )
        )

        // when
        val actualVideo = target.videoOfId(expectedId)

        // then
        assertNull(actualVideo)

        verify(1, getRequestedFor(urlPathEqualTo("/api/videos/$expectedId")))
    }

    @Test
    fun givenAVideo_whenReceive5xxFromServer_shouldReturnInternalError() {
        // given
        val expectedId = "any"
        val expectedErrorMessage = "Error observed from videos [resourceId: $expectedId] [status: 500]"
        val expectedRetryCount = 2

        val responseBody = writeValueAsString(mapOf("message" to "Internal Server Error"))

        stubFor(
            get(urlPathEqualTo("/api/videos/$expectedId"))
                .willReturn(
                    aResponse()
                        .withStatus(500)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(responseBody)
                )
        )

        // when
        val actualException = assertThrows<InternalErrorException> { target.videoOfId(expectedId) }

        // then
        assertEquals(expectedErrorMessage, actualException.message)

        verify(expectedRetryCount, getRequestedFor(urlPathEqualTo("/api/videos/$expectedId")))
    }

    @Test
    fun givenAVideo_whenReceiveTimeout_shouldReturnInternalError() {
        // given
        val expectedRetryCount = 2

        val cleanCode = cleanCode()

        val expectedErrorMessage = "Timeout observed from videos [resourceId: ${cleanCode.id}]"

        val responseBody = with(cleanCode) {
            writeValueAsString(
                VideoDTO(
                    id = id,
                    title = title,
                    description = description,
                    yearLaunched = launchedAt.value,
                    rating = rating.name,
                    duration = duration,
                    opened = opened,
                    published = published,
                    banner = imageResourceDTO(banner!!),
                    thumbnail = imageResourceDTO(thumbnail!!),
                    thumbnailHalf = imageResourceDTO(thumbnailHalf!!),
                    trailer = videoResourceDTO(trailer!!),
                    video = videoResourceDTO(this.video!!),
                    categoriesId = categories,
                    castMembersId = castMembers,
                    genresId = genres,
                    createdAt = createdAt.toString(),
                    updatedAt = updatedAt.toString()
                )
            )
        }

        stubFor(
            get(urlPathEqualTo("/api/videos/${cleanCode.id}"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withFixedDelay(600)
                        .withBody(responseBody)
                )
        )

        // when
        val actualException = assertThrows<InternalErrorException> { target.videoOfId(cleanCode.id) }

        // then
        assertEquals(expectedErrorMessage, actualException.message)

        verify(expectedRetryCount, getRequestedFor(urlPathEqualTo("/api/videos/${cleanCode.id}")))
    }

    @Test
    fun givenAVideo_whenBulkheadIsFull_shouldReturnError() {
        // given
        val cleanCode = cleanCode()

        val expectedErrorMessage = "Bulkhead 'videos' is full and does not permit further calls"

        acquireBulkheadPermission(VIDEO)

        // when
        val actualException = assertThrows<BulkheadFullException> { target.videoOfId(cleanCode.id) }

        // then
        assertEquals(expectedErrorMessage, actualException.message)

        releaseBulkheadPermission(VIDEO)
    }

    @Test
    fun givenServerError_whenIsMoreThanThreshold_shouldOpenCircuitBreaker() {
        // given
        val expectedId = "any"
        val expectedErrorMessage = "CircuitBreaker 'videos' is OPEN and does not permit further calls"

        val responseBody = writeValueAsString(mapOf("message" to "Internal Server Error"))

        stubFor(
            get(urlPathEqualTo("/api/videos/$expectedId"))
                .willReturn(
                    aResponse()
                        .withStatus(500)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(responseBody)
                )
        )

        // when
        assertThrows<InternalErrorException> { target.videoOfId(expectedId) }
        val actualException = assertThrows<CallNotPermittedException> { target.videoOfId(expectedId) }

        // then
        checkCircuitBreakerState(VIDEO, CircuitBreaker.State.OPEN)

        assertEquals(expectedErrorMessage, actualException.message)

        verify(3, getRequestedFor(urlPathEqualTo("/api/videos/$expectedId")))
    }

    @Test
    fun givenCall_whenCircuitBreakerIsOpen_shouldReturnError() {
        // given
        transitionToOpenState(VIDEO)

        val expectedId = "any"
        val expectedErrorMessage = "CircuitBreaker 'videos' is OPEN and does not permit further calls"

        // when
        val actualException = assertThrows<CallNotPermittedException> { target.videoOfId(expectedId) }

        // then
        checkCircuitBreakerState(VIDEO, CircuitBreaker.State.OPEN)

        assertEquals(expectedErrorMessage, actualException.message)

        verify(0, getRequestedFor(urlPathEqualTo("/api/videos/$expectedId")))
    }

    private fun videoResourceDTO(data: String) = VideoResourceDTO(
        id = IdUtils.uuid(),
        name = data,
        checksum = IdUtils.uuid(),
        location = data,
        encodedLocation = data,
        status = "processed"
    )

    private fun imageResourceDTO(data: String) = ImageResourceDTO(
        id = IdUtils.uuid(),
        name = data,
        checksum = IdUtils.uuid(),
        location = data
    )
}
