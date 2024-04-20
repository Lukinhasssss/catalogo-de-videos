package com.lukinhasssss.catalogo.infrastructure.category.models

import com.fasterxml.jackson.annotation.JsonProperty
import com.lukinhasssss.catalogo.domain.category.Category
import java.time.Instant

data class CategoryDTO(
    @JsonProperty(value = "id") val id: String,
    @JsonProperty(value = "name") val name: String,
    @JsonProperty(value = "description") val description: String? = null,
    @JsonProperty(value = "is_active") val active: Boolean = true,
    @JsonProperty(value = "created_at") val createdAt: Instant,
    @JsonProperty(value = "updated_at") val updatedAt: Instant,
    @JsonProperty(value = "deleted_at") val deletedAt: Instant? = null
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
