package com.lukinhasssss.catalogo.infrastructure.kafka.models.connect

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

enum class Operation(val op: String) {

    CREATE(op = "c"), UPDATE(op = "u"), DELETE("d");

    companion object {
        @JvmStatic
        @JsonCreator
        fun of(value: String) = entries.find { it.op == value }

        fun isDelete(op: Operation) = op == DELETE
    }

    @JsonValue // Para fazer a serializacao (converter de objeto para json)
    fun op() = op
}
