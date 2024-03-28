package com.lukinhasssss.catalogo.infrastructure.graphql

import com.lukinhasssss.catalogo.application.castmember.list.ListCastMemberOutput
import com.lukinhasssss.catalogo.application.castmember.list.ListCastMemberUseCase
import com.lukinhasssss.catalogo.domain.castmember.CastMemberSearchQuery
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class CastMemberGraphQLController(
    private val listCastMemberUseCase: ListCastMemberUseCase
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
}
