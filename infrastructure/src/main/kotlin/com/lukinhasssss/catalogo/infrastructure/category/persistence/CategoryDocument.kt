package com.lukinhasssss.catalogo.infrastructure.category.persistence

import com.lukinhasssss.catalogo.domain.category.Category
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import org.springframework.data.elasticsearch.annotations.InnerField
import org.springframework.data.elasticsearch.annotations.MultiField
import java.time.Instant

@Document(indexName = "categories")
data class CategoryDocument(

    @Id
    val id: String,

    @MultiField(
        mainField = Field(type = FieldType.Text, name = "name"),
        otherFields = [InnerField(type = FieldType.Keyword, suffix = ".keyword")]
    )
    val name: String,

    @Field(type = FieldType.Text, name = "description")
    val description: String? = null,

    @Field(type = FieldType.Boolean, name = "active")
    val active: Boolean,

    @Field(type = FieldType.Date, name = "created_at")
    val createdAt: Instant,

    @Field(type = FieldType.Date, name = "updated_at")
    val updatedAt: Instant,

    @Field(type = FieldType.Date, name = "deleted_at")
    val deletedAt: Instant? = null
) {

    companion object {
        fun from(aCategory: Category) = with(aCategory) {
            CategoryDocument(
                id = id,
                name = name,
                description = description,
                active = isActive,
                createdAt = createdAt,
                updatedAt = updatedAt,
                deletedAt = deletedAt
            )
        }
    }

    fun toCategory() = Category.with(
        anId = id,
        aName = name,
        aDescription = description,
        isActive = active,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )
}
