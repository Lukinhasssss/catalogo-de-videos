package com.lukinhasssss.catalogo.infrastructure.video

import com.lukinhasssss.catalogo.domain.pagination.Pagination
import com.lukinhasssss.catalogo.domain.video.Video
import com.lukinhasssss.catalogo.domain.video.VideoGateway
import com.lukinhasssss.catalogo.domain.video.VideoSearchQuery
import com.lukinhasssss.catalogo.infrastructure.video.VideoQueryBuilder.Companion.containingCastMembers
import com.lukinhasssss.catalogo.infrastructure.video.VideoQueryBuilder.Companion.containingCategories
import com.lukinhasssss.catalogo.infrastructure.video.VideoQueryBuilder.Companion.containingGenres
import com.lukinhasssss.catalogo.infrastructure.video.VideoQueryBuilder.Companion.launchedAtEquals
import com.lukinhasssss.catalogo.infrastructure.video.VideoQueryBuilder.Companion.onlyPublished
import com.lukinhasssss.catalogo.infrastructure.video.VideoQueryBuilder.Companion.ratingEquals
import com.lukinhasssss.catalogo.infrastructure.video.VideoQueryBuilder.Companion.titleOrDescriptionContaining
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
        val aQueryBuilder = VideoQueryBuilder(
            onlyPublished(),
            containingCategories(categories),
            containingCastMembers(castMembers),
            containingGenres(genres),
            launchedAtEquals(launchedAt),
            ratingEquals(rating),
            titleOrDescriptionContaining(terms)
        ).build()

        val query = NativeQuery.builder()
            .withQuery(aQueryBuilder)
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

    private fun buildSort(sort: String) = if (TITLE_PROP.equals(sort, ignoreCase = true)) sort.plus(KEYWORD) else sort
}
