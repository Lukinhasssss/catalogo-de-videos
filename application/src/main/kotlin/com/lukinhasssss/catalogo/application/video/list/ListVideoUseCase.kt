package com.lukinhasssss.catalogo.application.video.list

import com.lukinhasssss.catalogo.application.UseCase
import com.lukinhasssss.catalogo.domain.pagination.Pagination
import com.lukinhasssss.catalogo.domain.video.Video
import com.lukinhasssss.catalogo.domain.video.VideoGateway
import com.lukinhasssss.catalogo.domain.video.VideoSearchQuery
import java.time.Instant

class ListVideoUseCase(
    private val videoGateway: VideoGateway
) : UseCase<ListVideoUseCase.Input, Pagination<ListVideoUseCase.Output>>() {

    override fun execute(input: Input): Pagination<Output> = input.run {
        VideoSearchQuery(
            page = page,
            perPage = perPage,
            terms = terms,
            sort = sort,
            direction = direction,
            rating = rating,
            launchedAt = launchedAt,
            categories = categories,
            castMembers = castMembers,
            genres = genres
        ).run {
            videoGateway.findAll(this).map { Output.from(it) }
        }
    }

    data class Input(
        val page: Int,
        val perPage: Int,
        val terms: String,
        val sort: String,
        val direction: String,
        val rating: String? = null,
        val launchedAt: Int? = null,
        val categories: Set<String> = setOf(),
        val castMembers: Set<String> = setOf(),
        val genres: Set<String> = setOf()
    )

    data class Output(
        val id: String,
        val title: String,
        val description: String,
        val yearLaunched: Int,
        val rating: String,
        val duration: Double,
        val opened: Boolean,
        val published: Boolean,
        val banner: String? = null,
        val thumbnail: String? = null,
        val thumbnailHalf: String? = null,
        val trailer: String? = null,
        val video: String? = null,
        val categoriesId: Set<String>,
        val castMembersId: Set<String>,
        val genresId: Set<String>,
        val createdAt: Instant,
        val updatedAt: Instant
    ) {
        companion object {
            fun from(video: Video) = with(video) {
                Output(
                    id = id,
                    title = title,
                    description = description,
                    yearLaunched = launchedAt.value,
                    rating = rating.name,
                    duration = duration,
                    opened = opened,
                    published = published,
                    banner = banner,
                    thumbnail = thumbnail,
                    thumbnailHalf = thumbnailHalf,
                    trailer = trailer,
                    video = this.video,
                    categoriesId = categories,
                    castMembersId = castMembers,
                    genresId = genres,
                    createdAt = createdAt,
                    updatedAt = updatedAt
                )
            }
        }
    }
}
