package com.lukinhasssss.catalogo.infrastructure.video

import com.lukinhasssss.catalogo.infrastructure.authentication.GetClientCredentials
import com.lukinhasssss.catalogo.infrastructure.configuration.annotations.Videos
import com.lukinhasssss.catalogo.infrastructure.utils.HttpClient
import com.lukinhasssss.catalogo.infrastructure.video.models.VideoDTO
import io.github.resilience4j.bulkhead.annotation.Bulkhead
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

private const val VIDEO_CACHE_NAME = "admin-videos"
private const val VIDEO_CACHE_KEY = "#videoId"

@Component
@CacheConfig(cacheNames = [VIDEO_CACHE_NAME])
class VideoRestClient(
    @Videos private val restClient: RestClient,
    private val getClientCredentials: GetClientCredentials
) : VideoClient, HttpClient {

    companion object {
        const val NAMESPACE = "videos"
    }

    override fun namespace(): String = NAMESPACE

    // Resilience4j default order: Bulkhead -> TimeLimiter -> RateLimiter -> CircuitBreaker -> Retry
    @Retry(name = NAMESPACE)
    @Bulkhead(name = NAMESPACE)
    @CircuitBreaker(name = NAMESPACE)
    @Cacheable(key = VIDEO_CACHE_KEY, unless = "#result == null")
    override fun videoOfId(videoId: String?): VideoDTO? = doGet(videoId) {
        getClientCredentials.retrieve().let { token ->
            restClient.get()
                .uri("/videos/{id}", videoId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .retrieve()
                .onStatus(isNotFound(), notFoundHandler(videoId))
                .onStatus(is5xx(), serverErrorHandler(videoId))
                .body(VideoDTO::class.java)
        }
    }
}
