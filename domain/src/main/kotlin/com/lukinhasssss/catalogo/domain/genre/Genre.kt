package com.lukinhasssss.catalogo.domain.genre

import com.lukinhasssss.catalogo.domain.validation.Error
import com.lukinhasssss.catalogo.domain.validation.ValidationHandler
import com.lukinhasssss.catalogo.domain.validation.handler.ThrowsValidationHandler
import java.time.Instant

data class Genre(
    val id: String,
    val name: String,
    val isActive: Boolean,
    val categories: Set<String>,
    val createdAt: Instant,
    val updatedAt: Instant,
    val deletedAt: Instant? = null
) {
    init {
        validate(ThrowsValidationHandler())
    }

    companion object {
        fun with(
            id: String,
            name: String,
            isActive: Boolean,
            categories: Set<String>,
            createdAt: Instant,
            updatedAt: Instant,
            deletedAt: Instant? = null
        ) = Genre(
            id = id,
            name = name,
            isActive = isActive,
            categories = categories,
            createdAt = createdAt,
            updatedAt = updatedAt,
            deletedAt = deletedAt
        )

        fun with(aGenre: Genre) = with(aGenre) {
            Genre(
                id = id,
                name = name,
                isActive = isActive,
                categories = categories,
                createdAt = createdAt,
                updatedAt = updatedAt,
                deletedAt = deletedAt
            )
        }
    }

    fun validate(aHandler: ValidationHandler): Genre {
        if (id.isBlank()) {
            aHandler.append(anError = Error("'id' should not be empty"))
        }

        if (name.isBlank()) {
            aHandler.append(anError = Error("'name' should not be empty"))
        }

        return this
    }
}
