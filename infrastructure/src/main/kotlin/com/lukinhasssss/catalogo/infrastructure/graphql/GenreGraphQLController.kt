package com.lukinhasssss.catalogo.infrastructure.graphql

import com.lukinhasssss.catalogo.application.genre.list.ListGenreUseCase
import com.lukinhasssss.catalogo.application.genre.save.SaveGenreUseCase
import com.lukinhasssss.catalogo.infrastructure.genre.models.GenreDTO
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class GenreGraphQLController(
    private val listGenreUseCase: ListGenreUseCase,
    private val saveGenreUseCase: SaveGenreUseCase
) {

    @QueryMapping
    fun genres(
        @Argument search: String,
        @Argument page: Int,
        @Argument perPage: Int,
        @Argument sort: String,
        @Argument direction: String,
        @Argument categories: Set<String> = setOf()
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

    @MutationMapping
    fun saveGenre(@Argument(name = "input") input: GenreDTO): SaveGenreUseCase.Output =
        saveGenreUseCase.execute(input.toSaveGenreInput())
}
