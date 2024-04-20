package com.lukinhasssss.catalogo.infrastructure.genre.models

import com.lukinhasssss.catalogo.application.genre.save.SaveGenreUseCase
import java.time.Instant

data class GenreDTO(
    val id: String,
    val name: String,
    val active: Boolean,
    val categories: Set<String> = setOf(),
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant? = null
) {

    fun toSaveGenreInput() = SaveGenreUseCase.Input(
        id = id,
        name = name,
        isActive = active,
        categories = categories,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )
}
