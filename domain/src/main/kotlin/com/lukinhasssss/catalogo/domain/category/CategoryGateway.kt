package com.lukinhasssss.catalogo.domain.category

import com.lukinhasssss.catalogo.domain.pagination.Pagination

interface CategoryGateway {

    fun save(aCategory: Category): Category

    fun findById(anID: String): Category?

    fun findAll(aQuery: CategorySearchQuery): Pagination<Category>

    fun findAllById(ids: List<String>): List<Category>

    fun deleteById(anID: String)
}
