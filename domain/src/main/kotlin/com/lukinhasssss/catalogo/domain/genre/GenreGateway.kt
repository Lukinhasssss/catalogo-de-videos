package com.lukinhasssss.catalogo.domain.genre

import com.lukinhasssss.catalogo.domain.pagination.Pagination

interface GenreGateway {

    fun save(genre: Genre): Genre

    fun findById(id: String): Genre?

    fun findAll(aQuery: GenreSearchQuery): Pagination<Genre>

    fun findAllById(ids: List<String>): List<Genre>

    fun deleteById(id: String)
}
