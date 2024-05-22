package com.lukinhasssss.catalogo.infrastructure.category

import com.lukinhasssss.catalogo.domain.category.Category
import com.lukinhasssss.catalogo.domain.category.CategoryGateway
import com.lukinhasssss.catalogo.domain.category.CategorySearchQuery
import com.lukinhasssss.catalogo.domain.pagination.Pagination
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("development")
class CategoryInMemoryGateway(
    private val db: MutableMap<String, Category> = mutableMapOf()
) : CategoryGateway {

    override fun save(aCategory: Category): Category {
        db[aCategory.id] = aCategory
        return aCategory
    }

    override fun findById(anID: String): Category? {
        return db[anID]
    }

    override fun findAll(aQuery: CategorySearchQuery): Pagination<Category> = with(aQuery) {
        Pagination(
            currentPage = page,
            perPage = perPage,
            total = db.size.toLong(),
            items = db.values.toList()
        )
    }

    override fun findAllById(ids: Set<String>): List<Category> =
        db.filterKeys { it in ids }.values.toList()

    override fun deleteById(anID: String) {
        if (db.containsKey(anID)) db.remove(anID)
    }
}
