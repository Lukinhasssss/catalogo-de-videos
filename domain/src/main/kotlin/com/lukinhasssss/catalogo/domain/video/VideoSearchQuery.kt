package com.lukinhasssss.catalogo.domain.video

data class VideoSearchQuery(
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
