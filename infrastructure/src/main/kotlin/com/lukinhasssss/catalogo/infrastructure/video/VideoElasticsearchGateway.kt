package com.lukinhasssss.catalogo.infrastructure.video

import com.lukinhasssss.catalogo.domain.pagination.Pagination
import com.lukinhasssss.catalogo.domain.video.Video
import com.lukinhasssss.catalogo.domain.video.VideoGateway
import com.lukinhasssss.catalogo.domain.video.VideoSearchQuery
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!development")
class VideoElasticsearchGateway : VideoGateway {

    override fun save(video: Video): Video {
        TODO("Not yet implemented")
    }

    override fun findById(id: String): Video? {
        TODO("Not yet implemented")
    }

    override fun findAll(aQuery: VideoSearchQuery): Pagination<Video> {
        TODO("Not yet implemented")
    }

    override fun deleteById(id: String) {
        TODO("Not yet implemented")
    }
}
