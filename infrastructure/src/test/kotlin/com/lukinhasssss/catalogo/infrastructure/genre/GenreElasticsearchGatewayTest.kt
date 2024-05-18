package com.lukinhasssss.catalogo.infrastructure.genre

import com.lukinhasssss.catalogo.AbstractElasticsearchTest
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.domain.genre.Genre
import com.lukinhasssss.catalogo.domain.genre.GenreSearchQuery
import com.lukinhasssss.catalogo.domain.utils.IdUtils
import com.lukinhasssss.catalogo.domain.utils.InstantUtils
import com.lukinhasssss.catalogo.infrastructure.genre.persistence.GenreDocument
import com.lukinhasssss.catalogo.infrastructure.genre.persistence.GenreRepository
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
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

    @Test
    fun givenEmptyGenres_whenCallsFindAll_shouldReturnEmptyList() {
        // given
        val expectedPage = 0
        val expectedPerPage = 10
        val expectedTerms = ""
        val expectedSort = "name"
        val expectedDirection = "asc"
        val expectedTotal = 0

        val aQuery = GenreSearchQuery(
            page = expectedPage,
            perPage = expectedPerPage,
            terms = expectedTerms,
            sort = expectedSort,
            direction = expectedDirection
        )

        // when
        val actualOutput = genreGateway.findAll(aQuery)

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
            "mar, 0, 10, 1, 1, Marketing",
            "tec, 0, 10, 1, 1, Technology"
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
        mockGenres()

        val expectedSort = "name"
        val expectedDirection = "asc"

        val aQuery = GenreSearchQuery(
            page = expectedPage,
            perPage = expectedPerPage,
            terms = expectedTerms,
            sort = expectedSort,
            direction = expectedDirection
        )

        // when
        val actualOutput = genreGateway.findAll(aQuery)

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
            "c123, 0, 10, 1, 1, Marketing",
            "c456, 0, 10, 1, 1, Technology",
            ", 0, 10, 3, 3, Business"
        ]
    )
    fun givenValidCategory_whenCallsFindAll_shouldReturnElementsFiltered(
        expectedCategories: String?,
        expectedPage: Int,
        expectedPerPage: Int,
        expectedItemsCount: Int,
        expectedTotal: Long,
        expectedName: String
    ) {
        // given
        mockGenres()

        val expectedTerms = ""
        val expectedSort = "name"
        val expectedDirection = "asc"

        val aQuery = GenreSearchQuery(
            page = expectedPage,
            perPage = expectedPerPage,
            terms = expectedTerms,
            sort = expectedSort,
            direction = expectedDirection,
            categories = if (expectedCategories != null) setOf(expectedCategories) else setOf()
        )

        // when
        val actualOutput = genreGateway.findAll(aQuery)

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
            "name, asc, 0, 10, 3, 3, Business",
            "name, desc, 0, 10, 3, 3, Technology",
            "created_at, asc, 0, 10, 3, 3, Business",
            "created_at, desc, 0, 10, 3, 3, Marketing"
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
        mockGenres()

        val expectedTerms = ""

        val aQuery = GenreSearchQuery(
            page = expectedPage,
            perPage = expectedPerPage,
            terms = expectedTerms,
            sort = expectedSort,
            direction = expectedDirection
        )

        // when
        val actualOutput = genreGateway.findAll(aQuery)

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
            "0, 1, 1, 3, Business",
            "1, 1, 1, 3, Marketing",
            "2, 1, 1, 3, Technology",
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
        mockGenres()

        val expectedTerms = ""
        val expectedSort = "name"
        val expectedDirection = "asc"

        val aQuery = GenreSearchQuery(
            page = expectedPage,
            perPage = expectedPerPage,
            terms = expectedTerms,
            sort = expectedSort,
            direction = expectedDirection
        )

        // when
        val actualOutput = genreGateway.findAll(aQuery)

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
        val tech = genreRepository.save(
            GenreDocument.from(Fixture.Genres.tech()).copy(createdAt = InstantUtils.now())
        )

        genreRepository.save(
            GenreDocument.from(Fixture.Genres.marketing()).copy(createdAt = InstantUtils.now())
        )

        val business = genreRepository.save(
            GenreDocument.from(Fixture.Genres.business()).copy(createdAt = InstantUtils.now())
        )

        val expectedIds = listOf(tech.id, business.id)
        val expectedSize = expectedIds.size

        // when
        val actualOutput = genreGateway.findAllById(expectedIds)

        // then
        assertEquals(expectedSize, actualOutput.size)
        assertTrue(actualOutput.any { it.id == tech.id })
        assertTrue(actualOutput.any { it.id == business.id })
    }

    @Test
    fun givenInvalidIds_whenCallsFindAllByIds_shouldReturnEmptyList() {
        // given
        val tech = genreRepository.save(
            GenreDocument.from(Fixture.Genres.tech()).copy(createdAt = InstantUtils.now())
        )

        genreRepository.save(
            GenreDocument.from(Fixture.Genres.marketing()).copy(createdAt = InstantUtils.now())
        )

        val business = genreRepository.save(
            GenreDocument.from(Fixture.Genres.business()).copy(createdAt = InstantUtils.now())
        )

        val expectedIds = listOf(tech.id, "any", business.id)
        val expectedSize = 2

        // when
        val actualOutput = genreGateway.findAllById(expectedIds)

        // then
        assertEquals(expectedSize, actualOutput.size)
        assertTrue(actualOutput.any { it.id == tech.id })
        assertTrue(actualOutput.any { it.id == business.id })
    }

    @Test
    fun givenEmptyIds_whenCallsFindAllByIds_shouldReturnEmptyList() {
        // given
        val expectedIds = emptyList<String>()

        // when
        val actualOutput = genreGateway.findAllById(expectedIds)

        // then
        assertTrue(actualOutput.isEmpty())
    }

    private fun mockGenres() {
        genreRepository.save(GenreDocument.from(Fixture.Genres.business()))
        genreRepository.save(GenreDocument.from(Fixture.Genres.tech()))
        genreRepository.save(GenreDocument.from(Fixture.Genres.marketing()))
    }
}
