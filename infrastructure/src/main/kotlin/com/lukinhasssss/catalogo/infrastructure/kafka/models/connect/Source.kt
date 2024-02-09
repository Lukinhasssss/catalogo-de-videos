package com.lukinhasssss.catalogo.infrastructure.kafka.models.connect

import com.fasterxml.jackson.annotation.JsonProperty

data class Source(
    @JsonProperty("name")
    val name: String,

    @JsonProperty("db")
    val database: String,

    @JsonProperty("table")
    val table: String
)
