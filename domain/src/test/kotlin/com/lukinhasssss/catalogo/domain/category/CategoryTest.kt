package com.lukinhasssss.catalogo.domain.category

import com.lukinhasssss.catalogo.domain.UnitTest
import com.lukinhasssss.catalogo.domain.exception.DomainException
import com.lukinhasssss.catalogo.domain.utils.IdUtils
import com.lukinhasssss.catalogo.domain.utils.InstantUtils
import com.lukinhasssss.catalogo.domain.validation.handler.ThrowsValidationHandler
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CategoryTest : UnitTest() {

    @Test
    fun givenAValidParams_whenCallsWith_thenShouldInstantiateACategory() {
        // given
        val expectedId = IdUtils.uuid()
        val expectedName = "Filmes"
        val expectedDescription = "A categoria mais assistida"
        val expectedIsActive = true
        val expectedDate = InstantUtils.now()

        // when
        val actualCategory = Category.with(
            anId = expectedId,
            aName = expectedName,
            aDescription = expectedDescription,
            isActive = expectedIsActive,
            createdAt = expectedDate,
            updatedAt = expectedDate,
            deletedAt = expectedDate
        )

        // then
        with(actualCategory) {
            assertNotNull(this)
            assertEquals(expectedId, id)
            assertEquals(expectedName, name)
            assertEquals(expectedDescription, description)
            assertEquals(expectedIsActive, isActive)
            assertEquals(expectedDate, createdAt)
            assertEquals(expectedDate, updatedAt)
            assertEquals(expectedDate, deletedAt)
        }
    }

    @Test
    fun givenAValidParams_whenCallsWithCategory_thenShouldInstantiateACategory() {
        // given
        val expectedId = IdUtils.uuid()
        val expectedName = "Filmes"
        val expectedDescription = "A categoria mais assistida"
        val expectedIsActive = true
        val expectedDate = InstantUtils.now()

        val aCategory = Category.with(
            anId = expectedId,
            aName = expectedName,
            aDescription = expectedDescription,
            isActive = expectedIsActive,
            createdAt = expectedDate,
            updatedAt = expectedDate,
            deletedAt = expectedDate
        )

        // when
        val actualCategory = Category.with(aCategory)

        // then
        assertEquals(aCategory, actualCategory)
    }

    @Test
    fun givenAnInvalidId_whenCallsWithAndValidate_thenShouldReceiveError() {
        // given
        val expectedId = ""
        val expectedName = "Filmes"
        val expectedDescription = "A categoria mais assistida"
        val expectedIsActive = true
        val expectedDate = InstantUtils.now()
        val expectedErrorCount = 1
        val expectedErrorMessage = "'id' should not be empty"

        val actualCategory = Category.with(
            anId = expectedId,
            aName = expectedName,
            aDescription = expectedDescription,
            isActive = expectedIsActive,
            createdAt = expectedDate,
            updatedAt = expectedDate,
            deletedAt = expectedDate
        )

        // when
        val actualException = assertThrows<DomainException> {
            actualCategory.validate(ThrowsValidationHandler())
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
        val expectedDescription = "A categoria mais assistida"
        val expectedIsActive = true
        val expectedDate = InstantUtils.now()
        val expectedErrorCount = 1
        val expectedErrorMessage = "'name' should not be empty"

        val actualCategory = Category.with(
            anId = expectedId,
            aName = expectedName,
            aDescription = expectedDescription,
            isActive = expectedIsActive,
            createdAt = expectedDate,
            updatedAt = expectedDate,
            deletedAt = expectedDate
        )

        // when
        val actualException = assertThrows<DomainException> {
            actualCategory.validate(ThrowsValidationHandler())
        }

        // then
        with(actualException.errors) {
            assertEquals(expectedErrorCount, count())
            assertEquals(expectedErrorMessage, first().message)
        }
    }
}
