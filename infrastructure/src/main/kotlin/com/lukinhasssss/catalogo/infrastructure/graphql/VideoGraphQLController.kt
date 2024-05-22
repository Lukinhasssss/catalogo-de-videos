package com.lukinhasssss.catalogo.infrastructure.graphql

import com.lukinhasssss.catalogo.application.castmember.get.GetAllCastMembersByIdUseCase
import com.lukinhasssss.catalogo.application.category.get.GetAllCategoriesByIdUseCase
import com.lukinhasssss.catalogo.application.genre.get.GetAllGenresByIdUseCase
import com.lukinhasssss.catalogo.application.video.list.ListVideoUseCase
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.stereotype.Controller

@Controller
class VideoGraphQLController(
    private val listVideoUseCase: ListVideoUseCase,
    private val getAllCategoriesByIdUseCase: GetAllCategoriesByIdUseCase,
    private val getAllCastMembersByIdUseCase: GetAllCastMembersByIdUseCase,
    private val getAllGenresByIdUseCase: GetAllGenresByIdUseCase
) {

    @QueryMapping
    fun videos(
        @Argument search: String,
        @Argument page: Int,
        @Argument perPage: Int,
        @Argument sort: String,
        @Argument direction: String,
        @Argument rating: String? = null,
        @Argument yearLaunched: Int? = null,
        @Argument categories: Set<String> = setOf(),
        @Argument castMembers: Set<String> = setOf(),
        @Argument genres: Set<String> = setOf()
    ): List<ListVideoUseCase.Output> {
        val input = ListVideoUseCase.Input(
            page = page,
            perPage = perPage,
            terms = search,
            sort = sort,
            direction = direction,
            rating = rating,
            launchedAt = yearLaunched,
            categories = categories,
            castMembers = castMembers,
            genres = genres
        )

        return listVideoUseCase.execute(input).data
    }

    @SchemaMapping(typeName = "Video", field = "categories")
    fun categories(video: ListVideoUseCase.Output): List<GetAllCategoriesByIdUseCase.Output> =
        getAllCategoriesByIdUseCase.execute(GetAllCategoriesByIdUseCase.Input(video.categoriesId))

    @SchemaMapping(typeName = "Video", field = "castMembers")
    fun castMembers(video: ListVideoUseCase.Output): List<GetAllCastMembersByIdUseCase.Output> =
        getAllCastMembersByIdUseCase.execute(GetAllCastMembersByIdUseCase.Input(video.castMembersId))

    @SchemaMapping(typeName = "Video", field = "genres")
    fun genres(video: ListVideoUseCase.Output): List<GetAllGenresByIdUseCase.Output> =
        getAllGenresByIdUseCase.execute(GetAllGenresByIdUseCase.Input(video.genresId))
}
