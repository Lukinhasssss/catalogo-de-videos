package com.lukinhasssss.catalogo.infrastructure.video

import com.lukinhasssss.catalogo.domain.pagination.Pagination
import com.lukinhasssss.catalogo.domain.video.Video
import com.lukinhasssss.catalogo.domain.video.VideoGateway
import com.lukinhasssss.catalogo.domain.video.VideoSearchQuery
import com.lukinhasssss.catalogo.infrastructure.video.persistence.VideoDocument
import com.lukinhasssss.catalogo.infrastructure.video.persistence.VideoRepository
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Component
@Profile("!development")
class VideoElasticsearchGateway(
    private val videoRepository: VideoRepository
) : VideoGateway {

    override fun save(video: Video): Video {
        videoRepository.save(VideoDocument.from(video))
        return video
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
