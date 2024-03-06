package com.lukinhasssss.catalogo.infrastructure.authentication

import com.fasterxml.jackson.annotation.JsonProperty
import com.lukinhasssss.catalogo.domain.exception.InternalErrorException
import com.lukinhasssss.catalogo.infrastructure.authentication.AuthenticationGateway.AuthenticationResult
import com.lukinhasssss.catalogo.infrastructure.authentication.AuthenticationGateway.ClientCredentialsInput
import com.lukinhasssss.catalogo.infrastructure.authentication.AuthenticationGateway.RefreshTokenInput
import com.lukinhasssss.catalogo.infrastructure.configuration.annotations.Keycloak
import com.lukinhasssss.catalogo.infrastructure.configuration.properties.KeycloakProperties
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class KeycloakAuthenticationGateway(
    @Keycloak private val restClient: RestClient,
    private val keycloakProperties: KeycloakProperties
) : AuthenticationGateway {

    override fun login(input: ClientCredentialsInput): AuthenticationResult = with(input) {
        val requestBody = mapOf(
            "grant_type" to "client_credentials",
            "client_id" to clientId,
            "client_secret" to clientSecret
        ).toFormUrlEncoded()

        val output = restClient.post()
            .uri(keycloakProperties.tokenUri)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(requestBody)
            .retrieve()
            .body<KeycloakAuthenticationResult>()
            ?: throw InternalErrorException.with("Failed to create client credentials [clientId: $clientId]")

        AuthenticationResult(output.accessToken, output.refreshToken)
    }

    override fun refresh(input: RefreshTokenInput): AuthenticationResult = with(input) {
        val requestBody = mapOf(
            "grant_type" to "refresh_token",
            "client_id" to clientId,
            "client_secret" to clientSecret,
            "refresh_token" to refreshToken
        ).toFormUrlEncoded()

        val output = restClient.post()
            .uri(keycloakProperties.tokenUri)
            .accept(MediaType.APPLICATION_JSON)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(requestBody)
            .retrieve()
            .body<KeycloakAuthenticationResult>()
            ?: throw InternalErrorException.with("Failed to refresh client credentials [clientId: $clientId]")

        AuthenticationResult(output.accessToken, output.refreshToken)
    }

    data class KeycloakAuthenticationResult(
        @JsonProperty("access_token") val accessToken: String,
        @JsonProperty("refresh_token") val refreshToken: String = String()
    )

    private fun Map<String, String>.toFormUrlEncoded() = entries.joinToString("&") { "${it.key}=${it.value}" }
}
