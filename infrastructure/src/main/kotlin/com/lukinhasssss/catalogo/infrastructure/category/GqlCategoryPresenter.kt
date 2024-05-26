package com.lukinhasssss.catalogo.infrastructure.category

import com.lukinhasssss.catalogo.application.category.get.GetAllCategoriesByIdUseCase
import com.lukinhasssss.catalogo.application.category.list.ListCategoryOutput
import com.lukinhasssss.catalogo.domain.category.Category
import com.lukinhasssss.catalogo.infrastructure.category.models.GqlCategory

object GqlCategoryPresenter {
    fun present(output: ListCategoryOutput) = with(output) {
        GqlCategory(
            id = id,
            name = name,
            description = description
        )
    }

    fun present(output: GetAllCategoriesByIdUseCase.Output) = with(output) {
        GqlCategory(
            id = id,
            name = name,
            description = description
        )
    }

    fun present(output: Category) = with(output) {
        GqlCategory(
            id = id,
            name = name,
            description = description
        )
    }
}
