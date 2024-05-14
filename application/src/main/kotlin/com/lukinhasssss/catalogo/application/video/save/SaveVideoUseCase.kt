package com.lukinhasssss.catalogo.application.video.save

import com.lukinhasssss.catalogo.application.UseCase
import com.lukinhasssss.catalogo.application.video.save.SaveVideoUseCase.Input
import com.lukinhasssss.catalogo.application.video.save.SaveVideoUseCase.Output
import com.lukinhasssss.catalogo.domain.video.Video
import com.lukinhasssss.catalogo.domain.video.VideoGateway

class SaveVideoUseCase(
    private val videoGateway: VideoGateway
) : UseCase<Input, Output>() {

    override fun execute(input: Input): Output = input.run {
        videoGateway.save(
            Video.with(
                id = id,
                title = title,
                description = description,
                launchedAt = launchedAt,
                duration = duration,
                rating = rating,
                opened = opened,
                published = published,
                createdAt = createdAt,
                updatedAt = updatedAt,
                banner = banner,
                thumbnail = thumbnail,
                thumbnailHalf = thumbnailHalf,
                trailer = trailer,
                video = video,
                categories = categories,
                genres = genres,
                castMembers = castMembers
            )
        ).run { Output(id) }
    }

    data class Input(
        val id: String,
        val title: String,
        val description: String,
        val launchedAt: Int,
        val duration: Double,
        val rating: String,

        val opened: Boolean,
        var published: Boolean,

        val createdAt: String,
        var updatedAt: String,

        var banner: String? = null,
        var thumbnail: String? = null,
        var thumbnailHalf: String? = null,

        var trailer: String? = null,
        var video: String? = null,

        val categories: Set<String> = emptySet(),
        val genres: Set<String> = emptySet(),
        val castMembers: Set<String> = emptySet()
    )

    data class Output(val id: String)
}
