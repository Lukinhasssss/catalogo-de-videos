package com.lukinhasssss.catalogo.infrastructure.category

import com.lukinhasssss.catalogo.domain.category.Category
import com.lukinhasssss.catalogo.domain.utils.InstantUtils
import org.springframework.stereotype.Component

@Component
class NoOpCategoryGateway : CategoryGateway {

    override fun categoryOfId(anId: String?): Category? =
        Category.with(
            anId = anId!!,
            aName = "Lives",
            isActive = true,
            createdAt = InstantUtils.now(),
            updatedAt = InstantUtils.now()
        )
}
