package com.lukinhasssss.catalogo.infrastructure.category.models

import com.lukinhasssss.catalogo.domain.category.Category
import java.time.Instant

data class GqlCategoryInput(
    val id: String,
    val name: String,
    val description: String? = null,
    val active: Boolean = true,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant? = null
) {

    fun toCategory() = Category(
        id = id,
        name = name,
        description = description,
        isActive = active,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )
}
