package com.lukinhasssss.catalogo.infrastructure.authentication

import com.lukinhasssss.catalogo.infrastructure.authentication.AuthenticationGateway.ClientCredentialsInput
import com.lukinhasssss.catalogo.infrastructure.authentication.AuthenticationGateway.RefreshTokenInput
import com.lukinhasssss.catalogo.infrastructure.configuration.properties.KeycloakProperties
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater

@Component
class ClientCredentialsManager(
    private val authenticationGateway: AuthenticationGateway,
    private val keycloakProperties: KeycloakProperties
) : GetClientCredentials, RefreshClientCredentials {

    companion object {
        private val UPDATER = AtomicReferenceFieldUpdater.newUpdater(
            ClientCredentialsManager::class.java,
            ClientCredentials::class.java,
            "credentials"
        )
    }

    @Volatile
    private var credentials: ClientCredentials? = null

    override fun retrieve() = credentials!!.accessToken

    override fun refresh() = with(keycloakProperties) {
        val result = if (credentials == null) { login() } else { refreshToken() }
        UPDATER[this@ClientCredentialsManager] = ClientCredentials(clientId, result.accessToken, result.refreshToken)
    }

    private fun KeycloakProperties.login() =
        authenticationGateway.login(ClientCredentialsInput(clientId, clientSecret))

    private fun KeycloakProperties.refreshToken() = try {
        authenticationGateway.refresh(RefreshTokenInput(clientId, credentials!!.refreshToken))
    } catch (ex: RuntimeException) { login() }

    data class ClientCredentials(val clientId: String, val accessToken: String, val refreshToken: String)
}
