package com.lukinhasssss.catalogo.infrastructure.category

import com.lukinhasssss.catalogo.AbstractElasticsearchTest
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.infrastructure.category.persistence.CategoryDocument
import com.lukinhasssss.catalogo.infrastructure.category.persistence.CategoryRepository
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
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

    @Test
    fun givenAValidId_whenCallsDeleteById_shouldDeleteIt() {
        // given
        val aulas = Fixture.Categories.aulas

        categoryRepository.save(CategoryDocument.from(aulas))

        val expectedId = aulas.id

        assertTrue(categoryRepository.existsById(expectedId))

        // when
        categoryGateway.deleteById(expectedId)

        // then
        assertFalse(categoryRepository.existsById(expectedId))
    }

    @Test
    fun givenAnInvalidId_whenCallsDeleteById_shouldBeOk() {
        // when/then
        assertDoesNotThrow { categoryRepository.deleteById("any") }
    }

    @Test
    fun givenAValidId_whenCallsFindById_shouldRetrieveIt() {
        // given
        val talks = Fixture.Categories.talks

        categoryRepository.save(CategoryDocument.from(talks))

        val expectedId = talks.id

        assertTrue(categoryRepository.existsById(expectedId))

        // when
        val actualOutput = categoryGateway.findById(expectedId)

        // then
        with(actualOutput!!) {
            assertEquals(talks.id, id)
            assertEquals(talks.name, name)
            assertEquals(talks.description, description)
            assertEquals(talks.isActive, isActive)
            assertEquals(talks.createdAt, createdAt)
            assertEquals(talks.updatedAt, updatedAt)
            assertEquals(talks.deletedAt, deletedAt)
        }
    }

    @Test
    fun givenAnInvalidId_whenCallsFindById_shouldReturnNull() {
        // given
        val expectedId = "any"

        // when
        val actualOutput = categoryGateway.findById(expectedId)

        // then
        assertNull(actualOutput)
    }
}
