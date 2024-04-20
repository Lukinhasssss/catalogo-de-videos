package com.lukinhasssss.catalogo.infrastructure.genre

import com.lukinhasssss.catalogo.AbstractElasticsearchTest
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.domain.genre.Genre
import com.lukinhasssss.catalogo.domain.utils.IdUtils
import com.lukinhasssss.catalogo.domain.utils.InstantUtils
import com.lukinhasssss.catalogo.infrastructure.genre.persistence.GenreDocument
import com.lukinhasssss.catalogo.infrastructure.genre.persistence.GenreRepository
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GenreElasticsearchGatewayTest : AbstractElasticsearchTest() {

    @Autowired
    private lateinit var genreGateway: GenreElasticsearchGateway

    @Autowired
    private lateinit var genreRepository: GenreRepository

    @Test
    fun testInjection() {
        assertNotNull(genreGateway)
        assertNotNull(genreRepository)
    }

    @Test
    fun givenAValidActiveGenreWithCategories_whenCallsSave_shouldPersistIt() {
        // given
        val business = Genre.with(IdUtils.uuid(), "Business", false, setOf("c1", "c2"), InstantUtils.now(), InstantUtils.now(), InstantUtils.now())

        // when
        val actualOutput = genreGateway.save(business)

        // then
        assertEquals(business, actualOutput)

        genreRepository.findById(business.id).get().run {
            assertEquals(business.id, id)
            assertEquals(business.name, name)
            assertEquals(business.isActive, active)
            assertEquals(business.categories, categories)
            assertEquals(business.createdAt, createdAt)
            assertEquals(business.updatedAt, updatedAt)
            assertEquals(business.deletedAt, deletedAt)
        }
    }

    @Test
    fun givenAValidInactiveGenreWithoutCategories_whenCallsSave_shouldPersistIt() {
        // given
        val tech = Genre.with(IdUtils.uuid(), "Technology", true, setOf(), InstantUtils.now(), InstantUtils.now())

        // when
        val actualOutput = genreGateway.save(tech)

        // then
        assertEquals(tech, actualOutput)

        genreRepository.findById(tech.id).get().run {
            assertEquals(tech.id, id)
            assertEquals(tech.name, name)
            assertEquals(tech.isActive, active)
            assertEquals(tech.categories, categories)
            assertEquals(tech.createdAt, createdAt)
            assertEquals(tech.updatedAt, updatedAt)
            assertNull(deletedAt)
        }
    }

    @Test
    fun givenAValidId_whenCallsDeleteById_shouldDeleteIt() {
        // given
        val business = Fixture.Genres.business()

        genreRepository.save(GenreDocument.from(business))

        val expectedId = business.id

        assertTrue(genreRepository.existsById(expectedId))

        // when
        genreGateway.deleteById(expectedId)

        // then
        assertFalse(genreRepository.existsById(expectedId))
    }

    @Test
    fun givenAnInvalidId_whenCallsDeleteById_shouldBeOk() {
        // when/then
        assertDoesNotThrow { genreRepository.deleteById("any") }
    }

    @Test
    fun givenActiveGenreGenreWithCategories_whenCallsFindById_shouldRetrieveIt() {
        // given
        val business = Genre.with(IdUtils.uuid(), "Business", false, setOf("c1", "c2"), InstantUtils.now(), InstantUtils.now(), InstantUtils.now())

        genreRepository.save(GenreDocument.from(business))

        val expectedId = business.id

        assertTrue(genreRepository.existsById(expectedId))

        // when
        val actualOutput = genreGateway.findById(expectedId)

        // then
        assertEquals(business, actualOutput)
    }

    @Test
    fun givenInactiveGenreWithoutCategories_whenCallsFindById_shouldRetrieveIt() {
        // given
        val tech = Genre.with(IdUtils.uuid(), "Technology", true, setOf(), InstantUtils.now(), InstantUtils.now())

        genreRepository.save(GenreDocument.from(tech))

        val expectedId = tech.id

        assertTrue(genreRepository.existsById(expectedId))

        // when
        val actualOutput = genreGateway.findById(expectedId)

        // then
        assertEquals(tech, actualOutput)
    }

    @Test
    fun givenAnInvalidId_whenCallsFindById_shouldReturnNull() {
        // when
        val actualOutput = genreGateway.findById("any")

        // then
        assertNull(actualOutput)
    }
}
