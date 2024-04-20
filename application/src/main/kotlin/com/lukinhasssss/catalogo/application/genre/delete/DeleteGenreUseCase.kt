package com.lukinhasssss.catalogo.application.genre.delete

import com.lukinhasssss.catalogo.application.UnitUseCase
import com.lukinhasssss.catalogo.domain.genre.GenreGateway

class DeleteGenreUseCase(
    private val genreGateway: GenreGateway
) : UnitUseCase<String?>() {

    override fun execute(anIn: String?) = with(anIn) {
        if (anIn.isNullOrBlank()) return

        genreGateway.deleteById(this!!)
    }
}
