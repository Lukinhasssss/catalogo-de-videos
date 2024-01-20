package com.lukinhasssss.catalogo.infrastructure.category

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document

@Document(indexName = "categories")
data class CategoryDocument(

    @Id
    val id: String
)
