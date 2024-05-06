package com.lukinhasssss.catalogo.domain.genre

import com.lukinhasssss.catalogo.domain.UnitTest
import com.lukinhasssss.catalogo.domain.exception.DomainException
import com.lukinhasssss.catalogo.domain.utils.IdUtils
import com.lukinhasssss.catalogo.domain.utils.InstantUtils
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class GenreTest : UnitTest() {

    @Test
    fun givenAValidParams_whenCallsWith_thenShouldInstantiateAGenre() {
        // given
        val expectedId = IdUtils.uuid()
        val expectedName = "Business"
        val expectedIsActive = true
        val expectedCategories = setOf(IdUtils.uuid(), IdUtils.uuid())
        val expectedDate = InstantUtils.now()

        // when
        val actualGenre = Genre.with(
            expectedId,
            expectedName,
            expectedIsActive,
            expectedCategories,
            expectedDate,
            expectedDate,
            expectedDate
        )

        // then
        with(actualGenre) {
            assertNotNull(this)
            assertEquals(expectedId, id)
            assertEquals(expectedName, name)
            assertEquals(expectedCategories, categories)
            assertEquals(expectedIsActive, isActive)
            assertEquals(expectedDate, createdAt)
            assertEquals(expectedDate, updatedAt)
            assertEquals(expectedDate, deletedAt)
        }
    }

    @Test
    fun givenAValidParams_whenCallsWithGenre_thenShouldInstantiateAGenre() {
        // given
        val expectedId = IdUtils.uuid()
        val expectedName = "Business"
        val expectedIsActive = true
        val expectedCategories = setOf(IdUtils.uuid(), IdUtils.uuid())
        val expectedDate = InstantUtils.now()

        val aGenre = Genre.with(
            expectedId,
            expectedName,
            expectedIsActive,
            expectedCategories,
            expectedDate,
            expectedDate,
            expectedDate
        )

        // when
        val actualGenre = Genre.with(aGenre)

        // then
        with(actualGenre) {
            assertNotNull(this)
            assertEquals(expectedId, id)
            assertEquals(expectedName, name)
            assertEquals(expectedCategories, categories)
            assertEquals(expectedIsActive, isActive)
            assertEquals(expectedDate, createdAt)
            assertEquals(expectedDate, updatedAt)
            assertEquals(expectedDate, deletedAt)
        }
    }

    @Test
    fun givenEmptyCategories_whenCallsWithGenre_thenShouldInstantiateAGenreWithEmptyCategories() {
        // given
        val expectedId = IdUtils.uuid()
        val expectedName = "Business"
        val expectedIsActive = true
        val expectedCategories = emptySet<String>()
        val expectedDate = InstantUtils.now()

        // when
        val actualGenre = Genre.with(
            expectedId,
            expectedName,
            expectedIsActive,
            expectedCategories,
            expectedDate,
            expectedDate,
            expectedDate
        )

        // then
        with(actualGenre) {
            assertNotNull(this)
            assertEquals(expectedId, id)
            assertEquals(expectedName, name)
            assertEquals(expectedCategories, categories)
            assertEquals(expectedIsActive, isActive)
            assertEquals(expectedDate, createdAt)
            assertEquals(expectedDate, updatedAt)
            assertEquals(expectedDate, deletedAt)
        }
    }

    @Test
    fun givenAnInvalidId_whenCallsWithAndValidate_thenShouldReceiveError() {
        // given
        val expectedId = ""
        val expectedName = "Business"
        val expectedIsActive = true
        val expectedCategories = setOf(IdUtils.uuid(), IdUtils.uuid())
        val expectedDate = InstantUtils.now()
        val expectedErrorCount = 1
        val expectedErrorMessage = "'id' should not be empty"

        // when
        val actualException = assertThrows<DomainException> {
            Genre.with(expectedId, expectedName, expectedIsActive, expectedCategories, expectedDate, expectedDate, expectedDate)
        }

        // then
        with(actualException.errors) {
            assertEquals(expectedErrorCount, count())
            assertEquals(expectedErrorMessage, first().message)
        }
    }

    @Test
    fun givenAnInvalidName_whenCallsWithAndValidate_thenShouldReceiveError() {
        // given
        val expectedId = IdUtils.uuid()
        val expectedName = ""
        val expectedIsActive = true
        val expectedCategories = setOf(IdUtils.uuid(), IdUtils.uuid())
        val expectedDate = InstantUtils.now()
        val expectedErrorCount = 1
        val expectedErrorMessage = "'name' should not be empty"

        // when
        val actualException = assertThrows<DomainException> {
            Genre.with(expectedId, expectedName, expectedIsActive, expectedCategories, expectedDate, expectedDate, expectedDate)
        }

        // then
        with(actualException.errors) {
            assertEquals(expectedErrorCount, count())
            assertEquals(expectedErrorMessage, first().message)
        }
    }
}
