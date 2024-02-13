package com.lukinhasssss.catalogo.infrastructure.authentication

interface AuthenticationGateway {

    fun login(input: ClientCredentialsInput): AuthenticationResult

    fun refresh(input: RefreshTokenInput): AuthenticationResult

    data class AuthenticationResult(val accessToken: String, val refreshToken: String)

    data class ClientCredentialsInput(val clientId: String, val clientSecret: String)

    data class RefreshTokenInput(val clientId: String, val refreshToken: String)
}
