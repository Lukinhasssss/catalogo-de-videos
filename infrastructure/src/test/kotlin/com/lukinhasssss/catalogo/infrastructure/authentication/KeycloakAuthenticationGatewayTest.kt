package com.lukinhasssss.catalogo.infrastructure.authentication

import com.github.tomakehurst.wiremock.client.WireMock.aResponse
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.post
import com.github.tomakehurst.wiremock.client.WireMock.stubFor
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import com.lukinhasssss.catalogo.AbstractRestClientTest
import com.lukinhasssss.catalogo.domain.utils.IdUtils
import com.lukinhasssss.catalogo.infrastructure.authentication.AuthenticationGateway.ClientCredentialsInput
import com.lukinhasssss.catalogo.infrastructure.authentication.AuthenticationGateway.RefreshTokenInput
import com.lukinhasssss.catalogo.infrastructure.authentication.KeycloakAuthenticationGateway.KeycloakAuthenticationResult
import com.lukinhasssss.catalogo.infrastructure.configuration.json.Json
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import kotlin.test.assertEquals

class KeycloakAuthenticationGatewayTest : AbstractRestClientTest() {

    @Autowired
    private lateinit var gateway: KeycloakAuthenticationGateway

    @Test
    fun givenValidParams_whenCallsLogin_shouldReturnClientCredentials() {
        // given
        val expectedClientId = "any-client-id"
        val expectedClientSecret = IdUtils.uuid()
        val expectedAccessToken = "access"
        val expectedRefreshToken = "refresh"

        stubFor(
            post(urlPathEqualTo("/realms/test/protocol/openid-connect/token"))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(Json.writeValueAsString(KeycloakAuthenticationResult(expectedAccessToken, expectedRefreshToken)))
                )
        )

        // when
        val actualOutput = gateway.login(ClientCredentialsInput(expectedClientId, expectedClientSecret))

        // then
        assertEquals(expectedAccessToken, actualOutput.accessToken)
        assertEquals(expectedRefreshToken, actualOutput.refreshToken)
    }

    @Test
    fun givenValidParams_whenCallsRefresh_shouldReturnClientCredentials() {
        // given
        val expectedClientId = "any-client-id"
        val expectedClientSecret = IdUtils.uuid()
        val expectedAccessToken = "newAccessToken"
        val expectedRefreshToken = "newRefreshToken"

        stubFor(
            post(urlPathEqualTo("/realms/test/protocol/openid-connect/token"))
                .withHeader(HttpHeaders.ACCEPT, equalTo(MediaType.APPLICATION_JSON_VALUE))
                .withHeader(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_FORM_URLENCODED_VALUE))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(Json.writeValueAsString(KeycloakAuthenticationResult(expectedAccessToken, expectedRefreshToken)))
                )
        )

        // when
        val actualOutput = gateway.refresh(RefreshTokenInput(expectedClientId, expectedClientSecret, "refresh"))

        // then
        assertEquals(expectedAccessToken, actualOutput.accessToken)
        assertEquals(expectedRefreshToken, actualOutput.refreshToken)
    }
}
