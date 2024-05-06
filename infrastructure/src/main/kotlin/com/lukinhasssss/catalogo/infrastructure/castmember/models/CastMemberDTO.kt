package com.lukinhasssss.catalogo.infrastructure.castmember.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.lukinhasssss.catalogo.domain.castmember.CastMember
import com.lukinhasssss.catalogo.domain.castmember.CastMemberType
import java.time.Instant

data class CastMemberDTO(

    @JsonProperty(value = "id") val id: String,
    @JsonProperty(value = "name") val name: String,
    @JsonProperty(value = "type") val type: String,
    @JsonProperty(value = "created_at") val createdAt: Instant,
    @JsonProperty(value = "updated_at") val updatedAt: Instant
) {

    fun toCastMember() = CastMember.with(
        anId = id,
        aName = name,
        aType = CastMemberType.of(type),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
