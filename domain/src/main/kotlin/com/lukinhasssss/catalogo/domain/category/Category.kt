package com.lukinhasssss.catalogo.domain.category

import com.lukinhasssss.catalogo.domain.validation.Error
import com.lukinhasssss.catalogo.domain.validation.ValidationHandler
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

    fun validate(aHandler: ValidationHandler): Category {
        if (id.isBlank()) {
            aHandler.append(anError = Error("'id' should not be empty"))
        }

        if (name.isBlank()) {
            aHandler.append(anError = Error("'name' should not be empty"))
        }

        return this
    }
}
