package com.lukinhasssss.catalogo.infrastructure.genre

import com.lukinhasssss.catalogo.domain.genre.Genre
import com.lukinhasssss.catalogo.domain.genre.GenreGateway
import com.lukinhasssss.catalogo.domain.genre.GenreSearchQuery
import com.lukinhasssss.catalogo.domain.pagination.Pagination
import org.springframework.stereotype.Component

@Component
class GenreElasticsearchGateway : GenreGateway {

    override fun save(genre: Genre): Genre {
        TODO("Not yet implemented")
    }

    override fun findById(id: String): Genre? {
        TODO("Not yet implemented")
    }

    override fun findAll(aQuery: GenreSearchQuery): Pagination<Genre> {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: String) {
        TODO("Not yet implemented")
    }
}
