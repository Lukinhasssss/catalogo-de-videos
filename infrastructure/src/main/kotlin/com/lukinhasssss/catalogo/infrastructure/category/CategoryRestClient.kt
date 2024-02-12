package com.lukinhasssss.catalogo.infrastructure.category

import com.lukinhasssss.catalogo.infrastructure.category.models.CategoryDTO
import com.lukinhasssss.catalogo.infrastructure.utils.HttpClient
import io.github.resilience4j.bulkhead.annotation.Bulkhead
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker
import io.github.resilience4j.retry.annotation.Retry
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class CategoryRestClient(
    private val restClient: RestClient
) : HttpClient {

    companion object {
        const val NAMESPACE = "categories"
    }

    override fun namespace(): String = NAMESPACE

    // Resilience4j default order: Bulkhead -> TimeLimiter -> RateLimiter -> CircuitBreaker -> Retry
    @Retry(name = NAMESPACE)
    @Bulkhead(name = NAMESPACE)
    @CircuitBreaker(name = NAMESPACE)
    fun getById(categoryId: String?): CategoryDTO? = doGet(categoryId) {
        restClient.get()
            .uri("/{id}", categoryId)
            .retrieve()
            .onStatus(isNotFound(), notFoundHandler(categoryId))
            .onStatus(is5xx(), serverErrorHandler(categoryId))
            .body(CategoryDTO::class.java)
    }
}
