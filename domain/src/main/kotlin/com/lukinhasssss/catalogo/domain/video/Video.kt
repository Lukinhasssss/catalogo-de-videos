package com.lukinhasssss.catalogo.domain.video

import com.lukinhasssss.catalogo.domain.validation.Error
import com.lukinhasssss.catalogo.domain.validation.ValidationHandler
import com.lukinhasssss.catalogo.domain.validation.handler.ThrowsValidationHandler
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
    var published: Boolean,

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

    init {
        validate(ThrowsValidationHandler())

        if (banner.isNullOrBlank()) {
            published = false
        }

        if (thumbnail.isNullOrBlank()) {
            published = false
        }

        if (thumbnailHalf.isNullOrBlank()) {
            published = false
        }

        if (trailer.isNullOrBlank()) {
            published = false
        }

        if (video.isNullOrBlank()) {
            published = false
        }
    }

    companion object {
        fun with(
            id: String,
            title: String,
            description: String,
            launchedAt: Int,
            duration: Double,
            rating: String,
            opened: Boolean,
            published: Boolean,
            createdAt: String,
            updatedAt: String,
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
            launchedAt = Year.of(launchedAt),
            duration = duration,
            rating = Rating.of(rating),
            opened = opened,
            published = published,
            createdAt = Instant.parse(createdAt),
            updatedAt = Instant.parse(updatedAt),
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
            launchedAt = video.launchedAt.value,
            duration = video.duration,
            rating = video.rating.name,
            opened = video.opened,
            published = video.published,
            createdAt = video.createdAt.toString(),
            updatedAt = video.updatedAt.toString(),
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

    fun validate(handler: ValidationHandler) {
        if (id.isBlank()) {
            handler.append(Error("'id' should not be empty"))
        }

        if (title.isBlank()) {
            handler.append(Error("'title' should not be empty"))
        }

        if (description.isBlank()) {
            handler.append(Error("'description' should not be empty"))
        }
    }
}
