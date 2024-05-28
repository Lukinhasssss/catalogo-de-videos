package com.lukinhasssss.catalogo.infrastructure.video.persistence

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface VideoRepository : ElasticsearchRepository<VideoDocument, String>
