package com.lukinhasssss.catalogo.application.genre.get

import com.lukinhasssss.catalogo.application.UseCase
import com.lukinhasssss.catalogo.domain.genre.Genre
import com.lukinhasssss.catalogo.domain.genre.GenreGateway

class GetAllGenresByIdUseCase(
    private val genreGateway: GenreGateway
) : UseCase<GetAllGenresByIdUseCase.Input, List<GetAllGenresByIdUseCase.Output>>() {

    override fun execute(input: Input): List<Output> =
        if (input.ids.isEmpty()) {
            emptyList()
        } else {
            genreGateway.findAllById(input.ids).map { Output(it) }
        }

    data class Input(
        val ids: Set<String> = emptySet()
    )

    data class Output(
        val id: String,
        val name: String,
        val active: Boolean,
        val categories: Set<String> = setOf(),
        val createdAt: String,
        val updatedAt: String,
        val deletedAt: String? = null
    ) {
        constructor(genre: Genre) : this(
            id = genre.id,
            name = genre.name,
            active = genre.isActive,
            categories = genre.categories,
            createdAt = genre.createdAt.toString(),
            updatedAt = genre.updatedAt.toString(),
            deletedAt = genre.deletedAt.toString()
        )
    }
}
