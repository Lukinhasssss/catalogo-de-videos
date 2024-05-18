package com.lukinhasssss.catalogo.infrastructure.genre.persistence

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface GenreRepository : ElasticsearchRepository<GenreDocument, String>
