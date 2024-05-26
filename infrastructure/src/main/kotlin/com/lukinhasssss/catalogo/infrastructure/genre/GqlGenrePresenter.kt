package com.lukinhasssss.catalogo.infrastructure.genre

import com.lukinhasssss.catalogo.application.genre.get.GetAllGenresByIdUseCase
import com.lukinhasssss.catalogo.application.genre.list.ListGenreUseCase
import com.lukinhasssss.catalogo.infrastructure.genre.models.GqlGenre

object GqlGenrePresenter {
    fun present(output: ListGenreUseCase.Output) = with(output) {
        GqlGenre(
            id = id,
            name = name,
            active = active,
            categories = categories,
            createdAt = createdAt.toString(),
            updatedAt = updatedAt.toString(),
            deletedAt = deletedAt.toString()
        )
    }

    fun present(output: GetAllGenresByIdUseCase.Output) = with(output) {
        GqlGenre(
            id = id,
            name = name,
            active = active,
            categories = categories,
            createdAt = createdAt.toString(),
            updatedAt = updatedAt.toString(),
            deletedAt = deletedAt.toString()
        )
    }
}
