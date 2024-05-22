package com.lukinhasssss.catalogo.domain.video

import com.lukinhasssss.catalogo.domain.pagination.Pagination

interface VideoGateway {

    fun save(video: Video): Video

    fun findById(id: String): Video?

    fun findAll(aQuery: VideoSearchQuery): Pagination<Video>

    fun deleteById(id: String)
}
