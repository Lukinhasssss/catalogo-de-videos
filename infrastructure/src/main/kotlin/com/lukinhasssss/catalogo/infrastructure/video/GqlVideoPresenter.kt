package com.lukinhasssss.catalogo.infrastructure.video

import com.lukinhasssss.catalogo.application.video.list.ListVideoUseCase
import com.lukinhasssss.catalogo.infrastructure.video.models.GqlVideo

object GqlVideoPresenter {
    fun present(output: ListVideoUseCase.Output) = with(output) {
        GqlVideo(
            id = id,
            title = title,
            description = description,
            yearLaunched = yearLaunched,
            rating = rating,
            duration = duration,
            opened = opened,
            published = published,
            banner = banner,
            thumbnail = thumbnail,
            thumbnailHalf = thumbnailHalf,
            trailer = trailer,
            video = this.video,
            categoriesId = categoriesId,
            castMembersId = castMembersId,
            genresId = genresId,
            createdAt = createdAt.toString(),
            updatedAt = updatedAt.toString()
        )
    }
}
