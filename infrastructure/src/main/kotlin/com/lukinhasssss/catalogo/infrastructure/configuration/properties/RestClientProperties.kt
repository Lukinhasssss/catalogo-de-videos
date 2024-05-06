package com.lukinhasssss.catalogo.infrastructure.configuration.properties

data class RestClientProperties(
    var baseUrl: String = String(),
    var readTimeout: Int = 0
)
