package com.lukinhasssss.catalogo.infrastructure.video.models

data class GqlVideoInput(
    val id: String,
    val title: String,
    val description: String,
    val yearLaunched: Int,
    val duration: Double,
    val rating: String,

    val opened: Boolean,
    var published: Boolean,

    var banner: String? = null,
    var thumbnail: String? = null,
    var thumbnailHalf: String? = null,

    var trailer: String? = null,
    var video: String? = null,

    val categoriesId: Set<String> = emptySet(),
    val castMembersId: Set<String> = emptySet(),
    val genresId: Set<String> = emptySet(),

    val createdAt: String,
    var updatedAt: String
)
