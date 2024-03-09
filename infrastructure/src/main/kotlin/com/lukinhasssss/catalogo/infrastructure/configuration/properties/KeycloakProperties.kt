package com.lukinhasssss.catalogo.infrastructure.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "keycloak")
data class KeycloakProperties(
    var clientId: String = String(),
    var clientSecret: String = String(),
    var tokenUri: String = String()
)
