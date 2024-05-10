package com.lukinhasssss.catalogo.application.category.list

import com.lukinhasssss.catalogo.application.UseCase
import com.lukinhasssss.catalogo.domain.category.CategoryGateway
import com.lukinhasssss.catalogo.domain.category.CategorySearchQuery
import com.lukinhasssss.catalogo.domain.pagination.Pagination

class ListCategoryUseCase(
    private val categoryGateway: CategoryGateway
) : UseCase<CategorySearchQuery, Pagination<ListCategoryOutput>>() {

    override fun execute(input: CategorySearchQuery): Pagination<ListCategoryOutput> = with(input) {
        categoryGateway.findAll(this).map { ListCategoryOutput.from(it) }
    }
}
