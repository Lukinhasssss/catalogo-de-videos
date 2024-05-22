package com.lukinhasssss.catalogo.infrastructure.castmember.models

import com.lukinhasssss.catalogo.domain.castmember.CastMember
import com.lukinhasssss.catalogo.domain.castmember.CastMemberType
import java.time.Instant

data class GqlCastMemberInput(
    val id: String,
    val name: String,
    val type: String,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    fun toCastMember() = CastMember.with(
        anId = id,
        aName = name,
        aType = CastMemberType.of(type),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
