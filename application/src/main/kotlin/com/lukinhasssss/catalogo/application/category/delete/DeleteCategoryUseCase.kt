package com.lukinhasssss.catalogo.application.category.delete

import com.lukinhasssss.catalogo.application.UnitUseCase
import com.lukinhasssss.catalogo.domain.category.CategoryGateway

class DeleteCategoryUseCase(
    private val categoryGateway: CategoryGateway
) : UnitUseCase<String?>() {

    override fun execute(anIn: String?) = with(anIn) {
        if (this == null) return

        categoryGateway.deleteById(this)
    }
}
