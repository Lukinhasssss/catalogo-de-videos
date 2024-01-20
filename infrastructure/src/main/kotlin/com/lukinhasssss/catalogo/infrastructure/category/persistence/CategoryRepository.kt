package com.lukinhasssss.catalogo.infrastructure.category.persistence

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface CategoryRepository : ElasticsearchRepository<CategoryDocument, String>
