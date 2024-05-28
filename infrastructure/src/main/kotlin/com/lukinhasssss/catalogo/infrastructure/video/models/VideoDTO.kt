package com.lukinhasssss.catalogo.infrastructure.video.models

import com.lukinhasssss.catalogo.domain.video.Video

data class VideoDTO(
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
    val createdAt: String,
    val updatedAt: String
) {
    companion object {
        fun from(video: Video) = VideoDTO(
            id = video.id,
            title = video.title,
            description = video.description,
            yearLaunched = video.launchedAt.value,
            rating = video.rating.name,
            duration = video.duration,
            opened = video.opened,
            published = video.published,
            banner = video.banner,
            thumbnail = video.thumbnail,
            thumbnailHalf = video.thumbnailHalf,
            trailer = video.trailer,
            video = video.video,
            categoriesId = video.categories,
            castMembersId = video.castMembers,
            genresId = video.genres,
            createdAt = video.createdAt.toString(),
            updatedAt = video.updatedAt.toString()
        )
    }
}
