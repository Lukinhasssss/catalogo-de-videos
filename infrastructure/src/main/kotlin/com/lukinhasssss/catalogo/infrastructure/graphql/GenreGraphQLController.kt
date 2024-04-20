package com.lukinhasssss.catalogo.infrastructure.graphql

import com.lukinhasssss.catalogo.application.genre.list.ListGenreUseCase
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class GenreGraphQLController(
    private val listGenreUseCase: ListGenreUseCase
) {

    @QueryMapping
    fun genres(
        @Argument search: String,
        @Argument page: Int,
        @Argument perPage: Int,
        @Argument sort: String,
        @Argument direction: String,
        @Argument categories: Set<String>
    ): List<ListGenreUseCase.Output> {
        val input = ListGenreUseCase.Input(
            page = page,
            perPage = perPage,
            terms = search,
            sort = sort,
            direction = direction,
            categories = categories
        )

        return listGenreUseCase.execute(input).data
    }
}
