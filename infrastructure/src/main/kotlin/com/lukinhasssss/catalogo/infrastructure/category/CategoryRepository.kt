package com.lukinhasssss.catalogo.infrastructure.category

import com.lukinhasssss.catalogo.infrastructure.category.persistence.CategoryDocument
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface CategoryRepository : ElasticsearchRepository<CategoryDocument, String>
