package com.lukinhasssss.catalogo.infrastructure.category.models

data class GqlCategory(
    val id: String,
    val name: String,
    val description: String? = null
)
