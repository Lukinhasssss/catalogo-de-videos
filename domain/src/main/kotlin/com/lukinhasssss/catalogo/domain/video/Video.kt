package com.lukinhasssss.catalogo.domain.video

import java.time.Instant
import java.time.Year

data class Video(
    val id: String,
    val title: String,
    val description: String,
    val launchedAt: Year,
    val duration: Double,
    val rating: Rating,

    val opened: Boolean,
    val published: Boolean,

    val createdAt: Instant,
    var updatedAt: Instant,

    var banner: String? = null,
    var thumbnail: String? = null,
    var thumbnailHalf: String? = null,

    var trailer: String? = null,
    var video: String? = null,

    val categories: Set<String> = emptySet(),
    val genres: Set<String> = emptySet(),
    val castMembers: Set<String> = emptySet()
) {

    companion object {
        fun with(
            id: String,
            title: String,
            description: String,
            launchedAt: Year,
            duration: Double,
            rating: Rating,
            opened: Boolean,
            published: Boolean,
            createdAt: Instant,
            updatedAt: Instant,
            banner: String? = null,
            thumbnail: String? = null,
            thumbnailHalf: String? = null,
            trailer: String? = null,
            video: String? = null,
            categories: Set<String> = emptySet(),
            genres: Set<String> = emptySet(),
            castMembers: Set<String> = emptySet()
        ) = Video(
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

        fun with(video: Video) = with(
            id = video.id,
            title = video.title,
            description = video.description,
            launchedAt = video.launchedAt,
            duration = video.duration,
            rating = video.rating,
            opened = video.opened,
            published = video.published,
            createdAt = video.createdAt,
            updatedAt = video.updatedAt,
            banner = video.banner,
            thumbnail = video.thumbnail,
            thumbnailHalf = video.thumbnailHalf,
            trailer = video.trailer,
            video = video.video,
            categories = video.categories,
            genres = video.genres,
            castMembers = video.castMembers
        )
    }
}
