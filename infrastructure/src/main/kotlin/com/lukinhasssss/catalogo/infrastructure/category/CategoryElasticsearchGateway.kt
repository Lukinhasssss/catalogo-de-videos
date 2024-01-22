package com.lukinhasssss.catalogo.infrastructure.category

import com.lukinhasssss.catalogo.domain.category.Category
import com.lukinhasssss.catalogo.domain.category.CategoryGateway
import com.lukinhasssss.catalogo.domain.category.CategorySearchQuery
import com.lukinhasssss.catalogo.domain.pagination.Pagination
import com.lukinhasssss.catalogo.infrastructure.category.persistence.CategoryDocument
import com.lukinhasssss.catalogo.infrastructure.category.persistence.CategoryRepository
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class CategoryElasticsearchGateway(
    private val categoryRepository: CategoryRepository
) : CategoryGateway {

    override fun save(aCategory: Category): Category {
        categoryRepository.save(CategoryDocument.from(aCategory))
        return aCategory
    }

    override fun findById(anID: String): Category? =
        categoryRepository.findById(anID).getOrNull()?.run { toCategory() }

    override fun findAll(aQuery: CategorySearchQuery): Pagination<Category> = with(aQuery) {
        throw UnsupportedOperationException()
    }

    override fun deleteById(anID: String) = categoryRepository.deleteById(anID)
}
