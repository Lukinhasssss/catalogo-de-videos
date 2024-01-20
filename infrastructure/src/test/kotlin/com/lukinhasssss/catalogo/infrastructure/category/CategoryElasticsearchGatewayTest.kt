package com.lukinhasssss.catalogo.infrastructure.category

import com.lukinhasssss.catalogo.AbstractElasticsearchTest
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.infrastructure.category.persistence.CategoryRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

class CategoryElasticsearchGatewayTest : AbstractElasticsearchTest() {

    @Autowired
    private lateinit var categoryGateway: CategoryElasticsearchGateway

    @Autowired
    private lateinit var categoryRepository: CategoryRepository

    @Test
    fun testInjection() {
        assertNotNull(categoryGateway)
        assertNotNull(categoryRepository)
    }

    @Test
    fun givenAValidCategory_whenCallsSave_shouldPersistIt() {
        // given
        val aulas = Fixture.Categories.aulas

        // when
        val actualOutput = categoryGateway.save(aulas)

        // then
        assertEquals(aulas, actualOutput)

        categoryRepository.findById(aulas.id).get().run {
            assertEquals(aulas.id, id)
            assertEquals(aulas.name, name)
            assertEquals(aulas.description, description)
            assertEquals(aulas.isActive, active)
            assertEquals(aulas.createdAt, createdAt)
            assertEquals(aulas.updatedAt, updatedAt)
            assertEquals(aulas.deletedAt, deletedAt)
        }
    }
}
