package com.lukinhasssss.catalogo.infrastructure.configuration

import io.micrometer.common.KeyValues
import org.springframework.http.client.observation.ClientRequestObservationContext
import org.springframework.http.client.observation.ClientRequestObservationConvention

class RestClientObservationConfig : ClientRequestObservationConvention {

    override fun getName(): String {
        // will be used as the metric name
        return "rest_client"
    }

    override fun getContextualName(context: ClientRequestObservationContext): String {
        // will be used for the trace name
        return "http ${context.carrier?.method?.name().orEmpty()}"
    }

    override fun getLowCardinalityKeyValues(context: ClientRequestObservationContext): KeyValues {
        var keyValues = super.getLowCardinalityKeyValues(context)

        val request = context.carrier
        val response = context.response
        val error = context.error

        request?.let {
            val uuidRegex = "[0-9a-fA-F]{32}".toRegex()
            val uri = it.uri.path.replace(uuidRegex, "{id}")

            keyValues = keyValues.and("method", it.method.name())
                .and("uri", uri)
                .and("client.name", it.uri.host)
        }

        response?.let {
            keyValues = keyValues.and("status", it.statusText)
        }

        error?.let {
            keyValues = keyValues.and("outcome", it.localizedMessage)
                .and("error", it.javaClass.name)
        }

        return keyValues
    }

    override fun getHighCardinalityKeyValues(context: ClientRequestObservationContext): KeyValues {
        return super.getHighCardinalityKeyValues(context)
    }
}
