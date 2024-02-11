package com.lukinhasssss.catalogo.infrastructure.category

import com.lukinhasssss.catalogo.infrastructure.category.models.CategoryDTO
import com.lukinhasssss.catalogo.infrastructure.utils.HttpClient
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient

@Component
class CategoryRestClient(
    private val restClient: RestClient
) : HttpClient {

    companion object {
        private const val NAMESPACE = "categories"
    }

    override fun namespace(): String = NAMESPACE

    fun getById(categoryId: String?): CategoryDTO? = doGet(categoryId) {
        restClient.get()
            .uri("/{id}", categoryId)
            .retrieve()
            .onStatus(isNotFound(), notFoundHandler(categoryId))
            .onStatus(is5xx(), serverErrorHandler(categoryId))
            .body(CategoryDTO::class.java)
    }
}
