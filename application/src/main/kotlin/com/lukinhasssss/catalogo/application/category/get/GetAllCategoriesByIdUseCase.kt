package com.lukinhasssss.catalogo.application.category.get

import com.lukinhasssss.catalogo.application.UseCase
import com.lukinhasssss.catalogo.domain.category.Category
import com.lukinhasssss.catalogo.domain.category.CategoryGateway

class GetAllCategoriesByIdUseCase(
    private val categoryGateway: CategoryGateway
) : UseCase<GetAllCategoriesByIdUseCase.Input, List<GetAllCategoriesByIdUseCase.Output>>() {

    override fun execute(input: Input): List<Output> =
        if (input.ids.isEmpty()) {
            emptyList()
        } else {
            categoryGateway.findAllById(input.ids).map { Output(it) }
        }

    data class Input(
        val ids: Set<String> = emptySet()
    )

    data class Output(
        val id: String,
        val name: String,
        val description: String? = null
    ) {
        constructor(category: Category) : this(
            id = category.id,
            name = category.name,
            description = category.description
        )
    }
}
