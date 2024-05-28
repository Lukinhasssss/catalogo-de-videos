package com.lukinhasssss.catalogo.infrastructure.video.models

data class GqlVideo(
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
)
