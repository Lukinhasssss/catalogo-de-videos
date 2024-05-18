package com.lukinhasssss.catalogo.infrastructure.category

import com.lukinhasssss.catalogo.AbstractElasticsearchTest
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.domain.category.CategorySearchQuery
import com.lukinhasssss.catalogo.domain.utils.InstantUtils
import com.lukinhasssss.catalogo.infrastructure.category.persistence.CategoryDocument
import com.lukinhasssss.catalogo.infrastructure.category.persistence.CategoryRepository
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
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

    @Test
    fun givenEmptyCategories_whenCallsFindAll_shouldReturnEmptyList() {
        // given
        val expectedPage = 0
        val expectedPerPage = 10
        val expectedTerms = ""
        val expectedSort = "name"
        val expectedDirection = "asc"
        val expectedTotal = 0

        val aQuery = CategorySearchQuery(
            page = expectedPage,
            perPage = expectedPerPage,
            terms = expectedTerms,
            sort = expectedSort,
            direction = expectedDirection
        )

        // when
        val actualOutput = categoryGateway.findAll(aQuery)

        // then
        with(actualOutput) {
            assertEquals(expectedPage, meta.currentPage)
            assertEquals(expectedPerPage, meta.perPage)
            assertEquals(expectedTotal.toLong(), meta.total)
            assertEquals(expectedTotal, data.size)
        }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "aul, 0, 10, 1, 1, Aulas",
            "liv, 0, 10, 1, 1, Lives"
        ]
    )
    fun givenValidTerm_whenCallsFindAll_shouldReturnElementsFiltered(
        expectedTerms: String,
        expectedPage: Int,
        expectedPerPage: Int,
        expectedItemsCount: Int,
        expectedTotal: Long,
        expectedName: String
    ) {
        // given
        mockCategories()

        val expectedSort = "name"
        val expectedDirection = "asc"

        val aQuery = CategorySearchQuery(
            page = expectedPage,
            perPage = expectedPerPage,
            terms = expectedTerms,
            sort = expectedSort,
            direction = expectedDirection
        )

        // when
        val actualOutput = categoryGateway.findAll(aQuery)

        // then
        with(actualOutput) {
            assertEquals(expectedPage, meta.currentPage)
            assertEquals(expectedPerPage, meta.perPage)
            assertEquals(expectedTotal, meta.total)
            assertEquals(expectedItemsCount, data.size)
            assertEquals(expectedName, data[0].name)
        }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "name, asc, 0, 10, 3, 3, Aulas",
            "name, desc, 0, 10, 3, 3, Talks",
            "created_at, asc, 0, 10, 3, 3, Aulas",
            "created_at, desc, 0, 10, 3, 3, Lives"
        ]
    )
    fun givenValidSortAndDirection_whenCallsFindAll_shouldReturnElementsSorted(
        expectedSort: String,
        expectedDirection: String,
        expectedPage: Int,
        expectedPerPage: Int,
        expectedItemsCount: Int,
        expectedTotal: Long,
        expectedName: String
    ) {
        // given
        mockCategories()

        val expectedTerms = ""

        val aQuery = CategorySearchQuery(
            page = expectedPage,
            perPage = expectedPerPage,
            terms = expectedTerms,
            sort = expectedSort,
            direction = expectedDirection
        )

        // when
        val actualOutput = categoryGateway.findAll(aQuery)

        // then
        with(actualOutput) {
            assertEquals(expectedPage, meta.currentPage)
            assertEquals(expectedPerPage, meta.perPage)
            assertEquals(expectedTotal, meta.total)
            assertEquals(expectedItemsCount, data.size)
            assertEquals(expectedName, data[0].name)
        }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "0, 1, 1, 3, Aulas",
            "1, 1, 1, 3, Lives",
            "2, 1, 1, 3, Talks",
            "3, 1, 0, 3,"
        ]
    )
    fun givenValidPage_whenCallsFindAll_shouldReturnElementsPaged(
        expectedPage: Int,
        expectedPerPage: Int,
        expectedItemsCount: Int,
        expectedTotal: Long,
        expectedName: String?
    ) {
        // given
        mockCategories()

        val expectedTerms = ""
        val expectedSort = "name"
        val expectedDirection = "asc"

        val aQuery = CategorySearchQuery(
            page = expectedPage,
            perPage = expectedPerPage,
            terms = expectedTerms,
            sort = expectedSort,
            direction = expectedDirection
        )

        // when
        val actualOutput = categoryGateway.findAll(aQuery)

        // then
        with(actualOutput) {
            assertEquals(expectedPage, meta.currentPage)
            assertEquals(expectedPerPage, meta.perPage)
            assertEquals(expectedTotal, meta.total)
            assertEquals(expectedItemsCount, data.size)

            if (expectedName != null) {
                assertEquals(expectedName, data[0].name)
            }
        }
    }

    @Test
    fun givenValidIds_whenCallsFindAllByIds_shouldReturnElements() {
        // given
        val aulas = categoryRepository.save(
            CategoryDocument.from(Fixture.Categories.aulas).copy(createdAt = InstantUtils.now())
        )

        categoryRepository.save(
            CategoryDocument.from(Fixture.Categories.talks).copy(createdAt = InstantUtils.now())
        )

        val lives = categoryRepository.save(
            CategoryDocument.from(Fixture.Categories.lives).copy(createdAt = InstantUtils.now())
        )

        val expectedIds = listOf(aulas.id, lives.id)
        val expectedSize = expectedIds.size

        // when
        val actualOutput = categoryGateway.findAllById(expectedIds)

        // then
        assertEquals(expectedSize, actualOutput.size)
        assertTrue(actualOutput.any { it.id == aulas.id })
        assertTrue(actualOutput.any { it.id == lives.id })
    }

    @Test
    fun givenInvalidIds_whenCallsFindAllByIds_shouldReturnEmptyList() {
        // given
        val aulas = categoryRepository.save(
            CategoryDocument.from(Fixture.Categories.aulas).copy(createdAt = InstantUtils.now())
        )

        categoryRepository.save(
            CategoryDocument.from(Fixture.Categories.talks).copy(createdAt = InstantUtils.now())
        )

        val lives = categoryRepository.save(
            CategoryDocument.from(Fixture.Categories.lives).copy(createdAt = InstantUtils.now())
        )

        val expectedIds = listOf(aulas.id, "any", lives.id)
        val expectedSize = 2

        // when
        val actualOutput = categoryGateway.findAllById(expectedIds)

        // then
        assertEquals(expectedSize, actualOutput.size)
        assertTrue(actualOutput.any { it.id == aulas.id })
        assertTrue(actualOutput.any { it.id == lives.id })
    }

    @Test
    fun givenEmptyIds_whenCallsFindAllByIds_shouldReturnEmptyList() {
        // given
        val expectedIds = emptyList<String>()

        // when
        val actualOutput = categoryGateway.findAllById(expectedIds)

        // then
        assertTrue(actualOutput.isEmpty())
    }

    private fun mockCategories() {
        categoryRepository.save(
            CategoryDocument.from(Fixture.Categories.aulas).copy(createdAt = InstantUtils.now())
        )
        categoryRepository.save(
            CategoryDocument.from(Fixture.Categories.talks).copy(createdAt = InstantUtils.now())
        )
        categoryRepository.save(
            CategoryDocument.from(Fixture.Categories.lives).copy(createdAt = InstantUtils.now())
        )
    }
}
