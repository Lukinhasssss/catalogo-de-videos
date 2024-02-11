package com.lukinhasssss.catalogo.infrastructure.category

import com.lukinhasssss.catalogo.domain.category.Category

interface CategoryGateway {

    fun categoryOfId(anId: String?): Category?
}
