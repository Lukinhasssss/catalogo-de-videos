package com.lukinhasssss.catalogo.application.video.get

import com.lukinhasssss.catalogo.application.UseCase
import com.lukinhasssss.catalogo.domain.video.Video
import com.lukinhasssss.catalogo.domain.video.VideoGateway

class GetVideoUseCase(
    private val videoGateway: VideoGateway
) : UseCase<GetVideoUseCase.Input, GetVideoUseCase.Output?>() {

    override fun execute(input: Input): Output? =
        if (input.videoId.isBlank()) null else videoGateway.findById(input.videoId)?.let { Output.from(it) }

    data class Input(val videoId: String)

    data class Output(
        val id: String,
        val title: String,
        val description: String,
        val launchedAt: Int,
        val duration: Double,
        val rating: String,
        val opened: Boolean,
        val published: Boolean,
        val createdAt: String,
        val updatedAt: String,
        val banner: String? = null,
        val thumbnail: String? = null,
        val thumbnailHalf: String? = null,
        val trailer: String? = null,
        val video: String? = null,
        val categories: Set<String> = emptySet(),
        val genres: Set<String> = emptySet(),
        val castMembers: Set<String> = emptySet()
    ) {
        companion object {
            fun from(video: Video): Output = with(video) {
                Output(
                    id = id,
                    title = title,
                    description = description,
                    launchedAt = launchedAt.value,
                    duration = duration,
                    rating = rating.name,
                    opened = opened,
                    published = published,
                    createdAt = createdAt.toString(),
                    updatedAt = updatedAt.toString(),
                    banner = banner,
                    thumbnail = thumbnail,
                    thumbnailHalf = thumbnailHalf,
                    trailer = trailer,
                    video = this.video,
                    categories = categories,
                    genres = genres,
                    castMembers = castMembers
                )
            }
        }
    }
}
