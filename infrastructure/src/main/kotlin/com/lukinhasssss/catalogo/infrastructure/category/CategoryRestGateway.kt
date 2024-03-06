package com.lukinhasssss.catalogo.infrastructure.category

import com.lukinhasssss.catalogo.domain.category.Category
import com.lukinhasssss.catalogo.infrastructure.authentication.GetClientCredentials
import com.lukinhasssss.catalogo.infrastructure.category.models.CategoryDTO
import com.lukinhasssss.catalogo.infrastructure.configuration.annotations.Categories
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
@CacheConfig(cacheNames = ["admin-categories"])
class CategoryRestGateway(
    @Categories private val restClient: RestClient,
    private val getClientCredentials: GetClientCredentials
) : CategoryGateway, HttpClient {

    companion object {
        const val NAMESPACE = "categories"
    }

    override fun namespace(): String = NAMESPACE

    // Resilience4j default order: Bulkhead -> TimeLimiter -> RateLimiter -> CircuitBreaker -> Retry
    @Retry(name = NAMESPACE)
    @Bulkhead(name = NAMESPACE)
    @CircuitBreaker(name = NAMESPACE)
    @Cacheable(key = "#categoryId")
    override fun categoryOfId(categoryId: String?): Category? = doGet(categoryId) {
        getClientCredentials.retrieve().let { token ->
            restClient.get()
                .uri("/{id}", categoryId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .retrieve()
                .onStatus(isNotFound(), notFoundHandler(categoryId))
                .onStatus(is5xx(), serverErrorHandler(categoryId))
                .body(CategoryDTO::class.java)
                ?.toCategory()
        }
    }
}
