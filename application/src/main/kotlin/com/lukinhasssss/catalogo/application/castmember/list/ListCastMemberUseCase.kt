package com.lukinhasssss.catalogo.application.castmember.list

import com.lukinhasssss.catalogo.application.UseCase
import com.lukinhasssss.catalogo.domain.castmember.CastMemberGateway
import com.lukinhasssss.catalogo.domain.castmember.CastMemberSearchQuery
import com.lukinhasssss.catalogo.domain.pagination.Pagination

class ListCastMemberUseCase(
    private val castMemberGateway: CastMemberGateway
) : UseCase<CastMemberSearchQuery, Pagination<ListCastMemberOutput>>() {

    override fun execute(input: CastMemberSearchQuery): Pagination<ListCastMemberOutput> = with(input) {
        castMemberGateway.findAll(this).map { ListCastMemberOutput.from(it) }
    }
}
