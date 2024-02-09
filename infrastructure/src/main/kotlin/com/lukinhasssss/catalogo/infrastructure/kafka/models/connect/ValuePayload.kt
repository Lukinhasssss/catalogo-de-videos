package com.lukinhasssss.catalogo.infrastructure.kafka.models.connect

import com.fasterxml.jackson.annotation.JsonProperty

data class ValuePayload<T>(
    @JsonProperty("before")
    val before: T? = null,

    @JsonProperty("after")
    val after: T? = null,

    @JsonProperty("source")
    val source: Source,

    @JsonProperty("op")
    val operation: Operation
)
