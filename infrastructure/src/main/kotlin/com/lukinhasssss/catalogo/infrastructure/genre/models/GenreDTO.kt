package com.lukinhasssss.catalogo.infrastructure.genre.models

import com.lukinhasssss.catalogo.application.genre.save.SaveGenreUseCase
import com.lukinhasssss.catalogo.domain.genre.Genre
import java.time.Instant

data class GenreDTO(
    val id: String,
    val name: String,
    val isActive: Boolean,
    val categoriesId: Set<String> = setOf(),
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant? = null
) {

    fun toSaveGenreInput() = SaveGenreUseCase.Input(
        id = id,
        name = name,
        isActive = isActive,
        categories = categoriesId,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )

    companion object {
        fun from(aGenre: Genre): GenreDTO = with(aGenre) {
            GenreDTO(
                id = id,
                name = name,
                isActive = isActive,
                categoriesId = categories,
                createdAt = createdAt,
                updatedAt = updatedAt,
                deletedAt = deletedAt
            )
        }
    }
}
