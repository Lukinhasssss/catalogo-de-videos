package com.lukinhasssss.catalogo.application.castmember.list

import com.lukinhasssss.catalogo.domain.castmember.CastMember
import com.lukinhasssss.catalogo.domain.castmember.CastMemberType
import java.time.Instant

data class ListCastMemberOutput(
    val id: String,
    val name: String,
    val type: CastMemberType,
    val createdAt: Instant,
    val updatedAt: Instant
) {
    companion object {
        fun from(aMember: CastMember) = with(aMember) {
            ListCastMemberOutput(id, name, type, createdAt, updatedAt)
        }
    }
}
