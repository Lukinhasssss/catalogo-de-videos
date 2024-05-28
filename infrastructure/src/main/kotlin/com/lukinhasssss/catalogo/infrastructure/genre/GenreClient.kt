package com.lukinhasssss.catalogo.infrastructure.genre

import com.lukinhasssss.catalogo.infrastructure.genre.models.GenreDTO

fun interface GenreClient {
    fun genreOfId(genreId: String?): GenreDTO?
}
