package com.lukinhasssss.catalogo.infrastructure.category

import com.lukinhasssss.catalogo.domain.category.Category
import com.lukinhasssss.catalogo.domain.category.CategoryGateway
import com.lukinhasssss.catalogo.domain.category.CategorySearchQuery
import com.lukinhasssss.catalogo.domain.pagination.Pagination
import com.lukinhasssss.catalogo.infrastructure.category.persistence.CategoryDocument
import com.lukinhasssss.catalogo.infrastructure.category.persistence.CategoryRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.elasticsearch.core.SearchOperations
import org.springframework.data.elasticsearch.core.query.Criteria.where
import org.springframework.data.elasticsearch.core.query.CriteriaQuery
import org.springframework.data.elasticsearch.core.query.Query
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class CategoryElasticsearchGateway(
    private val categoryRepository: CategoryRepository,
    private val searchOperations: SearchOperations
) : CategoryGateway {

    companion object {
        private const val NAME_PROP = "name"
        private const val KEYWORD = ".keyword"
    }

    override fun save(aCategory: Category): Category {
        categoryRepository.save(CategoryDocument.from(aCategory))
        return aCategory
    }

    override fun findById(anID: String): Category? =
        categoryRepository.findById(anID).getOrNull()?.run { toCategory() }

    override fun findAll(aQuery: CategorySearchQuery): Pagination<Category> = with(aQuery) {
        val sort = Sort.by(Sort.Direction.fromString(direction), buildSort(sort))

        val query = PageRequest.of(page, perPage, sort).let {
            if (terms.isNotBlank()) {
                val criteria = where("name").contains(terms).or(where("description").contains(terms))

                CriteriaQuery(criteria, it)
            } else {
                Query.findAll().setPageable<Query>(it)
            }
        }

        val result = searchOperations.search(query, CategoryDocument::class.java)

        val total = result.totalHits

        val categories = result.map { it.content }.map { it.toCategory() }.toList()

        Pagination(
            currentPage = page,
            perPage = perPage,
            total = total,
            items = categories
        )
    }

    override fun findAllById(ids: Set<String>): List<Category> =
        if (ids.isEmpty()) {
            emptyList()
        } else {
            categoryRepository.findAllById(ids).map { it.toCategory() }
        }

    override fun deleteById(anID: String) = categoryRepository.deleteById(anID)

    private fun buildSort(sort: String) = when (sort) {
        NAME_PROP -> sort.plus(KEYWORD)
        else -> sort
    }
}
