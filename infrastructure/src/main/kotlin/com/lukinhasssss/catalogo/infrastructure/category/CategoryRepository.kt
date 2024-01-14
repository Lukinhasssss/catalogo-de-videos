package com.lukinhasssss.catalogo.infrastructure.category

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface CategoryRepository : ElasticsearchRepository<CategoryDocument, String>
