package com.lukinhasssss.catalogo.infrastructure.genre

import com.lukinhasssss.catalogo.domain.genre.Genre
import com.lukinhasssss.catalogo.domain.genre.GenreGateway
import com.lukinhasssss.catalogo.domain.genre.GenreSearchQuery
import com.lukinhasssss.catalogo.domain.pagination.Pagination

// @Component
class GenreInMemoryGateway(
    private val db: MutableMap<String, Genre> = mutableMapOf()
) : GenreGateway {

    override fun save(genre: Genre): Genre {
        db[genre.id] = genre
        return genre
    }

    override fun findById(id: String): Genre? {
        return db[id]
    }

    override fun findAll(aQuery: GenreSearchQuery): Pagination<Genre> = with(aQuery) {
        Pagination(
            currentPage = page,
            perPage = perPage,
            total = db.size.toLong(),
            items = db.values.toList()
        )
    }

    override fun deleteById(id: String) {
        if (db.containsKey(id)) db.remove(id)
    }
}
