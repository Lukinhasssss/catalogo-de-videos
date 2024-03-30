package com.lukinhasssss.catalogo.infrastructure.graphql

import com.lukinhasssss.catalogo.application.castmember.list.ListCastMemberOutput
import com.lukinhasssss.catalogo.application.castmember.list.ListCastMemberUseCase
import com.lukinhasssss.catalogo.application.castmember.save.SaveCastMemberUseCase
import com.lukinhasssss.catalogo.domain.castmember.CastMember
import com.lukinhasssss.catalogo.domain.castmember.CastMemberSearchQuery
import com.lukinhasssss.catalogo.infrastructure.castmember.models.CastMemberDTO
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class CastMemberGraphQLController(
    private val listCastMemberUseCase: ListCastMemberUseCase,
    private val saveCastMemberUseCase: SaveCastMemberUseCase
) {

    @QueryMapping
    fun castMembers(
        @Argument search: String,
        @Argument page: Int,
        @Argument perPage: Int,
        @Argument sort: String,
        @Argument direction: String
    ): List<ListCastMemberOutput> {
        val aQuery = CastMemberSearchQuery(
            page = page,
            perPage = perPage,
            terms = search,
            sort = sort,
            direction = direction
        )

        return listCastMemberUseCase.execute(aQuery).data
    }

    @MutationMapping
    fun saveCastMember(@Argument input: CastMemberDTO): CastMember =
        saveCastMemberUseCase.execute(input.toCastMember())
}
