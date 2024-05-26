package com.lukinhasssss.catalogo.infrastructure.video

import com.lukinhasssss.catalogo.domain.pagination.Pagination
import com.lukinhasssss.catalogo.domain.video.Video
import com.lukinhasssss.catalogo.domain.video.VideoGateway
import com.lukinhasssss.catalogo.domain.video.VideoSearchQuery
import org.springframework.stereotype.Component

// @Profile("development")
@Component
class VideoInMemoryGateway(
    private val db: MutableMap<String, Video> = mutableMapOf()
) : VideoGateway {

    override fun save(video: Video): Video {
        db[video.id] = video
        return video
    }

    override fun findById(id: String): Video? {
        return db[id]
    }

    override fun findAll(aQuery: VideoSearchQuery): Pagination<Video> = with(aQuery) {
        Pagination(
            currentPage = page,
            perPage = perPage,
            total = db.size.toLong(),
            items = db.values.toList()
        )
    }

    override fun deleteById(id: String) {
        db.remove(id)
    }
}
