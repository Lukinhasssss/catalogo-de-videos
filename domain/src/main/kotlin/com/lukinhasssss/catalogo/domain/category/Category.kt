package com.lukinhasssss.catalogo.domain.category

import java.time.Instant

data class Category(
    val id: String,
    val name: String,
    val description: String?,
    val isActive: Boolean,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant?
) {

    companion object {
        fun with(
            anId: String,
            aName: String,
            aDescription: String?,
            isActive: Boolean,
            createdAt: Instant,
            updatedAt: Instant,
            deletedAt: Instant?
        ) = Category(
            id = anId,
            name = aName,
            description = aDescription,
            isActive = isActive,
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt
        )

        fun with(aCategory: Category) = with(aCategory) {
            Category(
                id = id,
                name = name,
                description = description,
                isActive = isActive,
                createdAt = createdAt,
                updatedAt = updatedAt,
                deletedAt = deletedAt
            )
        }
    }
}
