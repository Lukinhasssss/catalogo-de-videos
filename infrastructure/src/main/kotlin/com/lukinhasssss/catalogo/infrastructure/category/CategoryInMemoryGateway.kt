package com.lukinhasssss.catalogo.infrastructure.category

import com.lukinhasssss.catalogo.domain.category.Category
import com.lukinhasssss.catalogo.domain.category.CategoryGateway
import com.lukinhasssss.catalogo.domain.category.CategorySearchQuery
import com.lukinhasssss.catalogo.domain.pagination.Pagination
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class CategoryInMemoryGateway(
    private val db: ConcurrentHashMap<String, Category> = ConcurrentHashMap()
) : CategoryGateway {

    override fun save(aCategory: Category): Category = with(aCategory) { db[id] = this; this }

    override fun findById(anID: String): Category? = db[anID]

    override fun findAll(aQuery: CategorySearchQuery): Pagination<Category> = with(aQuery) {
        val values = db.values
        Pagination(
            currentPage = page,
            perPage = perPage,
            total = values.size.toLong(),
            items = values.toList()
        )
    }

    override fun deleteById(anID: String?) {
        db.remove(anID)
    }
}
