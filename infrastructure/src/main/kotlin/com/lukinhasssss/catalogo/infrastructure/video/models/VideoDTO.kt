package com.lukinhasssss.catalogo.infrastructure.video.models

data class VideoDTO(
    val id: String,
    val title: String,
    val description: String,
    val yearLaunched: Int,
    val rating: String,
    val duration: Double,
    val opened: Boolean,
    val published: Boolean,
    val banner: ImageResourceDTO? = null,
    val thumbnail: ImageResourceDTO? = null,
    val thumbnailHalf: ImageResourceDTO? = null,
    val trailer: VideoResourceDTO? = null,
    val video: VideoResourceDTO? = null,
    val categoriesId: Set<String>,
    val castMembersId: Set<String>,
    val genresId: Set<String>,
    val createdAt: String,
    val updatedAt: String
)
