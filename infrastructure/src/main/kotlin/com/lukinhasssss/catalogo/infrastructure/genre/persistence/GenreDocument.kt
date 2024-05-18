package com.lukinhasssss.catalogo.infrastructure.genre.persistence

import com.lukinhasssss.catalogo.domain.genre.Genre
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import org.springframework.data.elasticsearch.annotations.InnerField
import org.springframework.data.elasticsearch.annotations.MultiField
import java.time.Instant

@Document(indexName = "genres")
data class GenreDocument(

    @Id
    val id: String,

    @MultiField(
        mainField = Field(type = FieldType.Text, name = "name"),
        otherFields = [InnerField(type = FieldType.Keyword, suffix = "keyword")]
    )
    val name: String,

    @Field(type = FieldType.Boolean, name = "active")
    val active: Boolean,

    @Field(type = FieldType.Keyword, name = "categories")
    val categories: Set<String> = setOf(),

    @Field(type = FieldType.Date, name = "created_at")
    val createdAt: Instant,

    @Field(type = FieldType.Date, name = "updated_at")
    val updatedAt: Instant,

    @Field(type = FieldType.Date, name = "deleted_at")
    val deletedAt: Instant? = null
) {

    companion object {
        fun from(aGenre: Genre) = with(aGenre) {
            GenreDocument(
                id = id,
                name = name,
                active = isActive,
                categories = categories,
                createdAt = createdAt,
                updatedAt = updatedAt,
                deletedAt = deletedAt
            )
        }
    }

    fun toGenre() = Genre.with(
        id = id,
        name = name,
        isActive = active,
        categories = categories,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt
    )
}
