package com.lukinhasssss.catalogo.infrastructure.genre

import com.lukinhasssss.catalogo.infrastructure.authentication.GetClientCredentials
import com.lukinhasssss.catalogo.infrastructure.configuration.annotations.Genres
import com.lukinhasssss.catalogo.infrastructure.genre.models.GenreDTO
import com.lukinhasssss.catalogo.infrastructure.utils.HttpClient
import io.github.resilience4j.bulkhead.annotation.Bulkhead
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.cache.annotation.CacheConfig
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
@CacheConfig(cacheNames = ["admin-genres"])
class GenreRestClient(
    @Genres private val restClient: RestClient,
    private val getClientCredentials: GetClientCredentials
) : GenreClient, HttpClient {

    companion object {
        const val NAMESPACE = "genres"
    }

    // Resilience4j default order: Bulkhead -> TimeLimiter -> RateLimiter -> CircuitBreaker -> Retry
    @Retry(name = NAMESPACE)
    @Bulkhead(name = NAMESPACE)
    @CircuitBreaker(name = NAMESPACE)
    @Cacheable(key = "#genreId")
    override fun genreOfId(genreId: String?): GenreDTO? = doGet(genreId) {
        getClientCredentials.retrieve().let { token ->
            restClient.get()
                .uri("/genres/{id}", genreId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .retrieve()
                .onStatus(isNotFound(), notFoundHandler(genreId))
                .onStatus(is5xx(), serverErrorHandler(genreId))
                .body(GenreDTO::class.java)
        }
    }

    override fun namespace() = NAMESPACE
}
