package com.lukinhasssss.catalogo.infrastructure.kafka.models.connect

import com.fasterxml.jackson.annotation.JsonProperty

data class MessageValue<T>(
    @JsonProperty("payload") val payload: ValuePayload<T>
)
