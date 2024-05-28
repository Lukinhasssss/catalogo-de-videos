package com.lukinhasssss.catalogo.infrastructure.category

import com.lukinhasssss.catalogo.domain.category.Category

interface CategoryClient {

    fun categoryOfId(categoryId: String?): Category?
}
