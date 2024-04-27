package com.lukinhasssss.catalogo.application.genre.delete

import com.lukinhasssss.catalogo.application.UnitUseCase
import com.lukinhasssss.catalogo.domain.genre.GenreGateway

class DeleteGenreUseCase(
    private val genreGateway: GenreGateway
) : UnitUseCase<DeleteGenreUseCase.Input>() {

    override fun execute(anIn: Input) = with(anIn) {
        if (this.genreId.isNullOrBlank()) return
        genreGateway.deleteById(this.genreId)
    }

    data class Input(
        val genreId: String?
    )
}
