package com.lukinhasssss.catalogo.application.genre.list

import com.lukinhasssss.catalogo.application.UseCase
import com.lukinhasssss.catalogo.domain.genre.Genre
import com.lukinhasssss.catalogo.domain.genre.GenreGateway
import com.lukinhasssss.catalogo.domain.genre.GenreSearchQuery
import com.lukinhasssss.catalogo.domain.pagination.Pagination
import java.time.Instant

class ListGenreUseCase(
    private val genreGateway: GenreGateway
) : UseCase<ListGenreUseCase.Input, Pagination<ListGenreUseCase.Output>>() {

    override fun execute(anIn: Input): Pagination<Output> = anIn.run {
        GenreSearchQuery(
            page = page,
            perPage = perPage,
            terms = terms,
            sort = sort,
            direction = direction,
            categories = categories
        ).run {
            genreGateway.findAll(this).map { Output.from(it) }
        }
    }

    data class Input(
        val page: Int,
        val perPage: Int,
        val terms: String,
        val sort: String,
        val direction: String,
        val categories: Set<String> = setOf()
    )

    data class Output(
        val id: String,
        val name: String,
        val active: Boolean,
        val categories: Set<String> = setOf(),
        val createdAt: Instant,
        val updatedAt: Instant,
        val deletedAt: Instant? = null
    ) {
        companion object {
            fun from(genre: Genre) = Output(
                id = genre.id,
                name = genre.name,
                active = genre.isActive,
                categories = genre.categories,
                createdAt = genre.createdAt,
                updatedAt = genre.updatedAt,
                deletedAt = genre.deletedAt
            )
        }
    }
}
