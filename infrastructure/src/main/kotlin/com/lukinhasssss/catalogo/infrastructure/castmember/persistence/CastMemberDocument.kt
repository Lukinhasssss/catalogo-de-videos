package com.lukinhasssss.catalogo.infrastructure.castmember.persistence

import com.lukinhasssss.catalogo.domain.castmember.CastMember
import com.lukinhasssss.catalogo.domain.castmember.CastMemberType
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import org.springframework.data.elasticsearch.annotations.InnerField
import org.springframework.data.elasticsearch.annotations.MultiField
import java.time.Instant

@Document(indexName = "cast_members")
data class CastMemberDocument(

    @Id
    val id: String,

    @MultiField(
        mainField = Field(type = FieldType.Text, name = "name"),
        otherFields = [InnerField(type = FieldType.Keyword, suffix = "keyword")]
    )
    val name: String,

    @Field(type = FieldType.Keyword, name = "type")
    val type: CastMemberType,

    @Field(type = FieldType.Date, name = "created_at")
    val createdAt: Instant,

    @Field(type = FieldType.Date, name = "updated_at")
    val updatedAt: Instant
) {

    companion object {
        fun from(castMember: CastMember) = with(castMember) {
            CastMemberDocument(
                id = id,
                name = name,
                type = type,
                createdAt = createdAt,
                updatedAt = updatedAt
            )
        }
    }

    fun toCastMember() = CastMember.with(
        anId = id,
        aName = name,
        aType = type,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
