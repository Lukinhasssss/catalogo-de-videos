package com.lukinhasssss.catalogo.infrastructure.category

import com.lukinhasssss.catalogo.domain.exception.InternalErrorException
import com.lukinhasssss.catalogo.infrastructure.category.models.CategoryDTO
import com.lukinhasssss.catalogo.infrastructure.exception.NotFoundException
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.client.ClientHttpResponse
import org.springframework.stereotype.Component
import org.springframework.web.client.ResourceAccessException
import org.springframework.web.client.RestClient
import java.net.http.HttpTimeoutException

@Component
class CategoryRestClient(
    private val restClient: RestClient
) {

    fun getById(categoryId: String?): CategoryDTO? = try {
        restClient.get()
            .uri("/{id}", categoryId)
            .retrieve()
            .onStatus(HttpStatus.NOT_FOUND::equals, handleNotFound(categoryId))
            .onStatus(HttpStatusCode::is5xxServerError, handleServerError(categoryId))
            .body(CategoryDTO::class.java)
    } catch (exception: NotFoundException) {
        null
    } catch (exception: ResourceAccessException) {
        handleResourceAcessException(exception, categoryId)
    }

    private fun handleNotFound(categoryId: String?) = { _: HttpRequest, _: ClientHttpResponse ->
        throw NotFoundException.with("A category of ID $categoryId was not found")
    }

    private fun handleServerError(categoryId: String?) = { _: HttpRequest, _: ClientHttpResponse ->
        throw InternalErrorException(message = "Failed to get Category of id $categoryId")
    }

    private fun handleResourceAcessException(exception: ResourceAccessException, categoryId: String?): Nothing =
        when (ExceptionUtils.getRootCause(exception)) {
            is HttpTimeoutException -> throw InternalErrorException.with("Timeout from category of ID $categoryId")
            else -> throw exception
        }
}
