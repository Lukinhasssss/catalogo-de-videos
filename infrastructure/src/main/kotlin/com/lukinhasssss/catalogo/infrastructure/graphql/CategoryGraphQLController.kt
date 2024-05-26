package com.lukinhasssss.catalogo.infrastructure.graphql

import com.lukinhasssss.catalogo.application.category.list.ListCategoryUseCase
import com.lukinhasssss.catalogo.application.category.save.SaveCategoryUseCase
import com.lukinhasssss.catalogo.domain.category.CategorySearchQuery
import com.lukinhasssss.catalogo.infrastructure.category.GqlCategoryPresenter
import com.lukinhasssss.catalogo.infrastructure.category.models.GqlCategory
import com.lukinhasssss.catalogo.infrastructure.category.models.GqlCategoryInput
import org.springframework.graphql.data.method.annotation.Argument
import org.springframework.graphql.data.method.annotation.MutationMapping
import org.springframework.graphql.data.method.annotation.QueryMapping
import org.springframework.stereotype.Controller

@Controller
class CategoryGraphQLController(
    private val listCategoryUseCase: ListCategoryUseCase,
    private val saveCategoryUseCase: SaveCategoryUseCase
) {

    @QueryMapping
    fun categories(
        @Argument search: String,
        @Argument page: Int,
        @Argument perPage: Int,
        @Argument sort: String,
        @Argument direction: String
    ): List<GqlCategory> {
        val aQuery = CategorySearchQuery(
            page = page,
            perPage = perPage,
            terms = search,
            sort = sort,
            direction = direction
        )

        return listCategoryUseCase.execute(aQuery).map(GqlCategoryPresenter::present).data
    }

    @MutationMapping
    fun saveCategory(@Argument input: GqlCategoryInput): GqlCategory =
        GqlCategoryPresenter.present(saveCategoryUseCase.execute(input.toCategory()))
}
