package com.lukinhasssss.catalogo.infrastructure.castmember

import com.lukinhasssss.catalogo.application.castmember.get.GetAllCastMembersByIdUseCase
import com.lukinhasssss.catalogo.application.castmember.list.ListCastMemberOutput
import com.lukinhasssss.catalogo.domain.castmember.CastMember
import com.lukinhasssss.catalogo.infrastructure.castmember.models.GqlCastMember

object GqlCastMemberPresenter {
    fun present(output: ListCastMemberOutput) = with(output) {
        GqlCastMember(
            id = id,
            name = name,
            type = type.name,
            createdAt = createdAt.toString(),
            updatedAt = updatedAt.toString()
        )
    }

    fun present(output: GetAllCastMembersByIdUseCase.Output) = with(output) {
        GqlCastMember(
            id = id,
            name = name,
            type = type.name,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    fun present(output: CastMember) = with(output) {
        GqlCastMember(
            id = id,
            name = name,
            type = type.name,
            createdAt = createdAt.toString(),
            updatedAt = updatedAt.toString()
        )
    }
}
