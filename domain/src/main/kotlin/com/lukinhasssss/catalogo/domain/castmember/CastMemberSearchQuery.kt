package com.lukinhasssss.catalogo.domain.castmember

data class CastMemberSearchQuery(
    val page: Int,
    val perPage: Int,
    val terms: String,
    val sort: String,
    val direction: String
)
