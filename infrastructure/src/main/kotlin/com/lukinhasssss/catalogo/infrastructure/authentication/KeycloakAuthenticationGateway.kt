package com.lukinhasssss.catalogo.infrastructure.authentication

import org.springframework.stereotype.Component

@Component
class KeycloakAuthenticationGateway : AuthenticationGateway {

    override fun login(): AuthenticationGateway.AuthenticationResult {
        TODO("Not yet implemented")
    }

    override fun refresh(): AuthenticationGateway.AuthenticationResult {
        TODO("Not yet implemented")
    }
}
