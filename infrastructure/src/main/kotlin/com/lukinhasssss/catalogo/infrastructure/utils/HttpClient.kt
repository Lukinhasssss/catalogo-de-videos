package com.lukinhasssss.catalogo.infrastructure.utils

import com.lukinhasssss.catalogo.domain.exception.InternalErrorException
import com.lukinhasssss.catalogo.infrastructure.exception.NotFoundException
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.http.HttpRequest
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.client.ClientHttpResponse
import org.springframework.web.client.ResourceAccessException
import java.net.http.HttpConnectTimeoutException
import java.net.http.HttpTimeoutException
import java.util.concurrent.TimeoutException
import java.util.function.Supplier

interface HttpClient {

    fun namespace(): String

    fun isNotFound() = HttpStatus.NOT_FOUND::equals
    fun is5xx() = HttpStatusCode::is5xxServerError

    fun notFoundHandler(id: String?) = { _: HttpRequest, _: ClientHttpResponse ->
        throw NotFoundException.with("Not found observed from ${namespace()} [resourceId: $id]")
    }

    fun serverErrorHandler(id: String?) = { _: HttpRequest, res: ClientHttpResponse ->
        throw InternalErrorException(
            message = "Error observed from ${namespace()} [resourceId: $id] [status: ${res.statusCode.value()}]"
        )
    }

    fun <T> doGet(id: String?, fn: Supplier<T>): T? =
        try {
            fn.get()
        } catch (ex: NotFoundException) {
            null
        } catch (ex: ResourceAccessException) {
            handleResourceAccessException(id, ex)
        } catch (t: Throwable) {
            handleThrowable(id, t)
        }

    private fun handleResourceAccessException(id: String?, ex: ResourceAccessException): Nothing =
        when (ExceptionUtils.getRootCause(ex)) {
            is HttpConnectTimeoutException ->
                throw InternalErrorException.with("ConnectTimeout observed from ${namespace()} [resourceId: $id]")
            is HttpTimeoutException, is TimeoutException ->
                throw InternalErrorException.with("Timeout observed from ${namespace()} [resourceId: $id]")
            else ->
                throw InternalErrorException.with("Error observed from ${namespace()} [resourceId: $id]")
        }

    private fun handleThrowable(id: String?, t: Throwable): Nothing =
        when (t) {
            is InternalErrorException -> throw t
            else -> throw InternalErrorException.with("Unhandled error observed from ${namespace()} [resourceId: $id]")
        }
}
