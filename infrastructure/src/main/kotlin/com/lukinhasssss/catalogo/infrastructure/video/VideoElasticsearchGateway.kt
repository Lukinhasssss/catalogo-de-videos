package com.lukinhasssss.catalogo.infrastructure.video

import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders
import com.lukinhasssss.catalogo.domain.pagination.Pagination
import com.lukinhasssss.catalogo.domain.video.Video
import com.lukinhasssss.catalogo.domain.video.VideoGateway
import com.lukinhasssss.catalogo.domain.video.VideoSearchQuery
import com.lukinhasssss.catalogo.infrastructure.video.persistence.VideoDocument
import com.lukinhasssss.catalogo.infrastructure.video.persistence.VideoRepository
import org.springframework.context.annotation.Profile
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.Direction
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.SearchOperations
import org.springframework.data.elasticsearch.core.search
import org.springframework.stereotype.Component

@Component
@Profile("!development")
class VideoElasticsearchGateway(
    private val videoRepository: VideoRepository,
    private val searchOperations: SearchOperations
) : VideoGateway {

    private companion object {
        const val TITLE_PROP = "title"
        const val KEYWORD = ".keyword"
    }

    override fun save(video: Video): Video {
        videoRepository.save(VideoDocument.from(video))
        return video
    }

    override fun findById(id: String): Video? =
        if (id.isBlank()) null else videoRepository.findById(id).map { it.toVideo() }.orElse(null)

    override fun findAll(aQuery: VideoSearchQuery): Pagination<Video> = with(aQuery) {
        val must = mutableListOf<Query>()

        must.add(QueryBuilders.term { term -> term.field("published").value(true) })

        if (categories.isNotEmpty()) {
            must.add(QueryBuilders.terms { terms -> terms.field("categories").terms { it.value(fieldValues(categories)) } })
        }

        if (castMembers.isNotEmpty()) {
            must.add(QueryBuilders.terms { terms -> terms.field("cast_members").terms { it.value(fieldValues(castMembers)) } })
        }

        if (genres.isNotEmpty()) {
            must.add(QueryBuilders.terms { terms -> terms.field("genres").terms { it.value(fieldValues(genres)) } })
        }

        if (launchedAt != null) {
            must.add(QueryBuilders.term { term -> term.field("launched_at").value(launchedAt!!.toLong()) })
        }

        if (rating.isNullOrBlank().not()) {
            must.add(QueryBuilders.term { term -> term.field("rating").value(rating) })
        }

        if (terms.isNotBlank()) {
            must.add(QueryBuilders.queryString { it.fields("title", "description").query("*$terms*") })
        }

        val query = NativeQuery.builder()
            .withQuery(QueryBuilders.bool { bool -> bool.must(must) })
            .withPageable(PageRequest.of(page, perPage, Sort.by(Direction.fromString(direction), buildSort(sort))))
            .build()

        val result = searchOperations.search<VideoDocument>(query)

        val total = result.totalHits
        val videos = result.map { it.content.toVideo() }.toList()

        Pagination(
            currentPage = page,
            perPage = perPage,
            total = total,
            items = videos
        )
    }

    override fun deleteById(id: String) = if (id.isBlank()) Unit else videoRepository.deleteById(id)

    private fun fieldValues(ids: Set<String>): List<FieldValue> = ids.map { FieldValue.of(it) }

    private fun buildSort(sort: String) = if (TITLE_PROP.equals(sort, ignoreCase = true)) sort.plus(KEYWORD) else sort
}
