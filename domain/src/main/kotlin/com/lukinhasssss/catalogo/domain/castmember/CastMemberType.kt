package com.lukinhasssss.catalogo.domain.castmember

enum class CastMemberType {
    ACTOR, DIRECTOR, UNKNOWN;

    companion object {
        fun of(type: String) = entries.find {
            it.name.equals(other = type, ignoreCase = true)
        } ?: UNKNOWN
    }
}
