package com.lukinhasssss.catalogo.infrastructure.category

import com.lukinhasssss.catalogo.AbstractElasticsearchTest
import com.lukinhasssss.catalogo.infrastructure.category.persistence.CategoryRepository
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CategoryElasticsearchGatewayTest : AbstractElasticsearchTest() {

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @Test
    fun testInjection() {
        assertNotNull(categoryRepository)
    }
}
