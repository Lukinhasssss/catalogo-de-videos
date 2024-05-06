package com.lukinhasssss.catalogo.domain.castmember

import com.lukinhasssss.catalogo.domain.validation.Error
import com.lukinhasssss.catalogo.domain.validation.ValidationHandler
import java.time.Instant

data class CastMember(
    val id: String,
    val name: String,
    val type: CastMemberType,
    val createdAt: Instant,
    val updatedAt: Instant
) {

    companion object {
        fun with(
            anId: String,
            aName: String,
            aType: CastMemberType,
            createdAt: Instant,
            updatedAt: Instant
        ) = CastMember(
            id = anId,
            name = aName,
            type = aType,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        fun with(aMember: CastMember) = with(aMember) {
            CastMember(
                id = id,
                name = name,
                type = type,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }

    fun validate(aHandler: ValidationHandler): CastMember {
        if (id.isBlank()) {
            aHandler.append(anError = Error("'id' should not be empty"))
        }

        if (name.isBlank()) {
            aHandler.append(anError = Error("'name' should not be empty"))
        }

        return this
    }
}
