package com.lukinhasssss.catalogo.infrastructure.video.persistence

import com.lukinhasssss.catalogo.domain.video.Video
import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import org.springframework.data.elasticsearch.annotations.InnerField
import org.springframework.data.elasticsearch.annotations.MultiField

@Document(indexName = "videos")
data class VideoDocument(

    @Id
    val id: String,

    @MultiField(
        mainField = Field(type = FieldType.Text, name = "title"),
        otherFields = [ InnerField(suffix = "keyword", type = FieldType.Keyword) ]
    )
    val title: String,

    @Field(type = FieldType.Text, name = "description")
    val description: String,

    @Field(type = FieldType.Integer, name = "launched_at")
    val launchedAt: Int,

    @Field(type = FieldType.Double, name = "duration")
    val duration: Double,

    @Field(type = FieldType.Keyword, name = "rating")
    val rating: String,

    @Field(type = FieldType.Boolean, name = "opened")
    val opened: Boolean,

    @Field(type = FieldType.Boolean, name = "published")
    val published: Boolean,

    @Field(type = FieldType.Keyword, name = "banner")
    val banner: String? = null,

    @Field(type = FieldType.Keyword, name = "thumbnail")
    val thumbnail: String? = null,

    @Field(type = FieldType.Keyword, name = "thumbnail_half")
    val thumbnailHalf: String? = null,

    @Field(type = FieldType.Keyword, name = "trailer")
    val trailer: String? = null,

    @Field(type = FieldType.Keyword, name = "video")
    val video: String? = null,

    @Field(type = FieldType.Keyword, name = "categories")
    val categories: Set<String> = setOf(),

    @Field(type = FieldType.Keyword, name = "cast_members")
    val castMembers: Set<String> = setOf(),

    @Field(type = FieldType.Keyword, name = "genres")
    val genres: Set<String> = setOf(),

    @Field(type = FieldType.Date, name = "created_at")
    val createdAt: String,

    @Field(type = FieldType.Date, name = "updated_at")
    val updatedAt: String
) {

    companion object {
        fun from(aVideo: Video) = with(aVideo) {
            VideoDocument(
                id = id,
                title = title,
                description = description,
                launchedAt = launchedAt.value,
                duration = duration,
                rating = rating.name,
                opened = opened,
                published = published,
                banner = banner,
                thumbnail = thumbnail,
                thumbnailHalf = thumbnailHalf,
                trailer = trailer,
                video = video,
                categories = categories,
                castMembers = castMembers,
                genres = genres,
                createdAt = createdAt.toString(),
                updatedAt = updatedAt.toString()
            )
        }
    }

    fun toVideo() = Video.with(
        id = id,
        title = title,
        description = description,
        launchedAt = launchedAt,
        duration = duration,
        rating = rating,
        opened = opened,
        published = published,
        banner = banner,
        thumbnail = thumbnail,
        thumbnailHalf = thumbnailHalf,
        trailer = trailer,
        video = video,
        categories = categories,
        castMembers = castMembers,
        genres = genres,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
