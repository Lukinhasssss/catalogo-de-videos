package com.lukinhasssss.catalogo.application.genre.save

import com.lukinhasssss.catalogo.application.UseCase
import com.lukinhasssss.catalogo.domain.genre.Genre
import com.lukinhasssss.catalogo.domain.genre.GenreGateway
import java.time.Instant

class SaveGenreUseCase(
    private val genreGateway: GenreGateway
) : UseCase<SaveGenreUseCase.Input, SaveGenreUseCase.Output>() {

    override fun execute(anIn: Input): Output = anIn.run {
        genreGateway.save(
            Genre.with(
                id = id,
                name = name,
                isActive = isActive,
                categories = categories,
                createdAt = createdAt,
                updatedAt = updatedAt,
                deletedAt = deletedAt
            )
        ).run { Output(id) }
    }

    data class Input(
        val id: String,
        val name: String,
        val isActive: Boolean,
        val categories: Set<String> = setOf(),
        val createdAt: Instant,
        val updatedAt: Instant,
        val deletedAt: Instant? = null
    )

    data class Output(
        val id: String
    )
}
