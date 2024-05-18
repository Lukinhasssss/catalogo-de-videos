package com.lukinhasssss.catalogo.application.castmember.get

import com.lukinhasssss.catalogo.application.UseCase
import com.lukinhasssss.catalogo.domain.castmember.CastMember
import com.lukinhasssss.catalogo.domain.castmember.CastMemberGateway
import com.lukinhasssss.catalogo.domain.castmember.CastMemberType

class GetAllCastMembersByIdUseCase(
    private val castMemberGateway: CastMemberGateway
) : UseCase<GetAllCastMembersByIdUseCase.Input, List<GetAllCastMembersByIdUseCase.Output>>() {

    override fun execute(input: Input): List<Output> =
        if (input.ids.isEmpty()) {
            emptyList()
        } else {
            castMemberGateway.findAllById(input.ids).map { Output(it) }
        }

    data class Input(
        val ids: Set<String> = emptySet()
    )

    data class Output(
        val id: String,
        val name: String,
        val type: CastMemberType,
        val createdAt: String,
        val updatedAt: String
    ) {
        constructor(castMember: CastMember) : this(
            id = castMember.id,
            name = castMember.name,
            type = castMember.type,
            createdAt = castMember.createdAt.toString(),
            updatedAt = castMember.updatedAt.toString()
        )
    }
}
