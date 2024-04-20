package com.lukinhasssss.catalogo.infrastructure.genre

import com.lukinhasssss.catalogo.domain.genre.Genre
import com.lukinhasssss.catalogo.domain.genre.GenreGateway
import com.lukinhasssss.catalogo.domain.genre.GenreSearchQuery
import com.lukinhasssss.catalogo.domain.pagination.Pagination
import com.lukinhasssss.catalogo.infrastructure.genre.persistence.GenreDocument
import com.lukinhasssss.catalogo.infrastructure.genre.persistence.GenreRepository
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class GenreElasticsearchGateway(
    private val genreRepository: GenreRepository
) : GenreGateway {

    override fun save(genre: Genre): Genre {
        genreRepository.save(GenreDocument.from(genre))
        return genre
    }

    override fun findById(id: String): Genre? =
        genreRepository.findById(id).getOrNull()?.run { toGenre() }

    override fun findAll(aQuery: GenreSearchQuery): Pagination<Genre> {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: String) = genreRepository.deleteById(id)
}
