package com.lukinhasssss.catalogo.application.category.list

import com.lukinhasssss.catalogo.domain.category.Category

data class ListCategoryOutput(
    val id: String,
    val name: String
) {
    companion object {
        fun from(aCategory: Category) = with(aCategory) {
            ListCategoryOutput(id = id, name = name)
        }
    }
}
