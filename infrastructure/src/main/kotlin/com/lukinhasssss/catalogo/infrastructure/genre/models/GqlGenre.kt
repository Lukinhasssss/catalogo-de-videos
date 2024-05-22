package com.lukinhasssss.catalogo.infrastructure.genre.models

data class GqlGenre(
    val id: String,
    val name: String,
    val active: Boolean,
    val categories: Set<String> = setOf(),
    val createdAt: String,
    val updatedAt: String,
    val deletedAt: String? = null
)
