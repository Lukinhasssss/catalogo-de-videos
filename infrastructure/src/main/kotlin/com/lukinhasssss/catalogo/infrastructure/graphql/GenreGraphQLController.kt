package com.lukinhasssss.catalogo.infrastructure.graphql

import com.lukinhasssss.catalogo.application.genre.list.ListGenreUseCase
import com.lukinhasssss.catalogo.application.genre.save.SaveGenreUseCase
import com.lukinhasssss.catalogo.infrastructure.configuration.security.Roles
import com.lukinhasssss.catalogo.infrastructure.genre.GqlGenrePresenter
import com.lukinhasssss.catalogo.infrastructure.genre.models.GqlGenre
import com.lukinhasssss.catalogo.infrastructure.genre.models.GqlGenreInput
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller

@Controller
class GenreGraphQLController(
    private val listGenreUseCase: ListGenreUseCase,
    private val saveGenreUseCase: SaveGenreUseCase
) {

    @QueryMapping
    @Secured(Roles.ADMIN, Roles.GENRES, Roles.SUBSCRIBER)
    fun genres(
        @Argument search: String,
        @Argument page: Int,
        @Argument perPage: Int,
        @Argument sort: String,
        @Argument direction: String,
        @Argument categories: Set<String> = setOf()
    ): List<GqlGenre> {
        val input = ListGenreUseCase.Input(
            page = page,
            perPage = perPage,
            terms = search,
            sort = sort,
            direction = direction,
            categories = categories
        )

        return listGenreUseCase.execute(input).map(GqlGenrePresenter::present).data
    }

    @MutationMapping
    @Secured(Roles.ADMIN, Roles.GENRES, Roles.SUBSCRIBER)
    fun saveGenre(@Argument(name = "input") input: GqlGenreInput): SaveGenreUseCase.Output =
        saveGenreUseCase.execute(input.toSaveGenreInput())
}
