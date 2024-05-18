package com.lukinhasssss.catalogo.application.video.list

import com.lukinhasssss.catalogo.application.UseCase
import com.lukinhasssss.catalogo.domain.pagination.Pagination
import com.lukinhasssss.catalogo.domain.video.Video
import com.lukinhasssss.catalogo.domain.video.VideoGateway
import com.lukinhasssss.catalogo.domain.video.VideoSearchQuery

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
        val rating: String,
        val launchedAt: Int,
        val categories: Set<String> = setOf(),
        val castMembers: Set<String> = setOf(),
        val genres: Set<String> = setOf()
    )

    data class Output(
        val id: String,
        val title: String,
        val description: String,
        val published: Boolean,
        val launchYear: Int,
        val rating: String,
        val banner: String? = null,
        val thumbnail: String? = null,
        val thumbnailHalf: String? = null,
        val trailer: String? = null,
        val video: String? = null,
        val categories: Set<String>,
        val castMembers: Set<String>,
        val genres: Set<String>
    ) {
        companion object {
            fun from(video: Video) = with(video) {
                Output(
                    id = id,
                    title = title,
                    description = description,
                    published = published,
                    launchYear = launchedAt.value,
                    rating = rating.name,
                    banner = banner,
                    thumbnail = thumbnail,
                    thumbnailHalf = thumbnailHalf,
                    trailer = trailer,
                    video = this.video,
                    categories = categories,
                    castMembers = castMembers,
                    genres = genres
                )
            }
        }
    }
}
