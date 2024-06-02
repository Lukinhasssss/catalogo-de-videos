package com.lukinhasssss.catalogo.infrastructure.graphql

import com.lukinhasssss.catalogo.application.castmember.get.GetAllCastMembersByIdUseCase
import com.lukinhasssss.catalogo.application.category.get.GetAllCategoriesByIdUseCase
import com.lukinhasssss.catalogo.application.genre.get.GetAllGenresByIdUseCase
import com.lukinhasssss.catalogo.application.video.list.ListVideoUseCase
import com.lukinhasssss.catalogo.application.video.save.SaveVideoUseCase
import com.lukinhasssss.catalogo.infrastructure.castmember.GqlCastMemberPresenter
import com.lukinhasssss.catalogo.infrastructure.castmember.models.GqlCastMember
import com.lukinhasssss.catalogo.infrastructure.category.GqlCategoryPresenter
import com.lukinhasssss.catalogo.infrastructure.category.models.GqlCategory
import com.lukinhasssss.catalogo.infrastructure.configuration.security.Roles
import com.lukinhasssss.catalogo.infrastructure.genre.GqlGenrePresenter
import com.lukinhasssss.catalogo.infrastructure.genre.models.GqlGenre
import com.lukinhasssss.catalogo.infrastructure.video.GqlVideoPresenter
import com.lukinhasssss.catalogo.infrastructure.video.models.GqlVideo
import com.lukinhasssss.catalogo.infrastructure.video.models.GqlVideoInput
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.graphql.data.method.annotation.SchemaMapping
import org.springframework.security.access.annotation.Secured
import org.springframework.stereotype.Controller

@Controller
class VideoGraphQLController(
    private val listVideoUseCase: ListVideoUseCase,
    private val saveVideoUseCase: SaveVideoUseCase,
    private val getAllCategoriesByIdUseCase: GetAllCategoriesByIdUseCase,
    private val getAllCastMembersByIdUseCase: GetAllCastMembersByIdUseCase,
    private val getAllGenresByIdUseCase: GetAllGenresByIdUseCase
) {

    // @PreAuthorize("hasAnyRole('${Roles.ADMIN}', '${Roles.VIDEOS}', '${Roles.SUBSCRIBER}')")
    @QueryMapping
    @Secured(Roles.ADMIN, Roles.VIDEOS, Roles.SUBSCRIBER)
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
    ): List<GqlVideo> {
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

        return listVideoUseCase.execute(input).map {
            GqlVideoPresenter.present(it)
        }.data
    }

    @SchemaMapping(typeName = "Video", field = "categories")
    @Secured(Roles.ADMIN, Roles.VIDEOS, Roles.SUBSCRIBER)
    fun categories(video: GqlVideo): List<GqlCategory> =
        getAllCategoriesByIdUseCase.execute(GetAllCategoriesByIdUseCase.Input(video.categoriesId))
            .map(GqlCategoryPresenter::present)

    @SchemaMapping(typeName = "Video", field = "castMembers")
    @Secured(Roles.ADMIN, Roles.VIDEOS, Roles.SUBSCRIBER)
    fun castMembers(video: GqlVideo): List<GqlCastMember> =
        getAllCastMembersByIdUseCase.execute(GetAllCastMembersByIdUseCase.Input(video.castMembersId))
            .map(GqlCastMemberPresenter::present)

    @SchemaMapping(typeName = "Video", field = "genres")
    @Secured(Roles.ADMIN, Roles.VIDEOS, Roles.SUBSCRIBER)
    fun genres(video: GqlVideo): List<GqlGenre> =
        getAllGenresByIdUseCase.execute(GetAllGenresByIdUseCase.Input(video.genresId))
            .map(GqlGenrePresenter::present)

    @MutationMapping
    @Secured(Roles.ADMIN, Roles.VIDEOS)
    fun saveVideo(@Argument input: GqlVideoInput): SaveVideoUseCase.Output = with(input) {
        val saveVideoInput = SaveVideoUseCase.Input(
            id = id,
            title = title,
            description = description,
            launchedAt = yearLaunched,
            duration = duration,
            rating = rating,
            opened = opened,
            published = published,
            banner = banner,
            thumbnail = thumbnail,
            thumbnailHalf = thumbnailHalf,
            trailer = trailer,
            video = video,
            categories = categoriesId,
            castMembers = castMembersId,
            genres = genresId,
            createdAt = createdAt,
            updatedAt = updatedAt
        )

        saveVideoUseCase.execute(saveVideoInput)
    }
}
