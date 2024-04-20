package com.lukinhasssss.catalogo.domain.genre

data class GenreSearchQuery(
    val page: Int,
    val perPage: Int,
    val terms: String,
    val sort: String,
    val direction: String,
    val categories: Set<String>
)
