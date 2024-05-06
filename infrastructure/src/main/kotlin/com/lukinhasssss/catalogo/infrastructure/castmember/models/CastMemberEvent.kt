package com.lukinhasssss.catalogo.infrastructure.castmember.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.lukinhasssss.catalogo.domain.castmember.CastMember
import com.lukinhasssss.catalogo.domain.castmember.CastMemberType
import java.time.Instant

data class CastMemberEvent(
    @JsonProperty("id") val id: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("type") val type: String,
    @JsonProperty("created_at") val createdAt: Long,
    @JsonProperty("updated_at") val updatedAt: Long
) {

    companion object {
        fun from(castMember: CastMember) = CastMemberEvent(
            id = castMember.id,
            name = castMember.name,
            type = castMember.type.name,
            createdAt = castMember.createdAt.toEpochMilli(),
            updatedAt = castMember.updatedAt.toEpochMilli()
        )
    }

    fun toCastMember(): CastMember {
        // Timestamp(createdAt / 1_000).toInstant() --> This is the same as Instant.ofEpochMilli(createdAt)

        return CastMember(
            id = id,
            name = name,
            type = CastMemberType.valueOf(type),
            createdAt = Instant.ofEpochMilli(createdAt),
            updatedAt = Instant.ofEpochMilli(updatedAt)
        )
    }
}
