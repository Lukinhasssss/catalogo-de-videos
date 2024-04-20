package com.lukinhasssss.catalogo.application.genre.save

import com.lukinhasssss.catalogo.application.UseCaseTest
import com.lukinhasssss.catalogo.domain.exception.DomainException
import com.lukinhasssss.catalogo.domain.genre.GenreGateway
import com.lukinhasssss.catalogo.domain.utils.IdUtils
import com.lukinhasssss.catalogo.domain.utils.InstantUtils
import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SaveGenreUseCaseTest : UseCaseTest() {

    @InjectMockKs
    private lateinit var useCase: SaveGenreUseCase

    @MockK
    private lateinit var genreGateway: GenreGateway

    @Test
    fun givenAValidInput_whenCallsSave_shouldPersistIt() {
        // given
        val expectedId = IdUtils.uuid()
        val expectedName = "Business"
        val expectedIsActive = true
        val expectedCategories = setOf(IdUtils.uuid(), IdUtils.uuid())
        val expectedDate = InstantUtils.now()

        every { genreGateway.save(any()) } answers { firstArg() }

        // when
        val input = SaveGenreUseCase.Input(
            id = expectedId,
            name = expectedName,
            isActive = expectedIsActive,
            categories = expectedCategories,
            createdAt = expectedDate,
            updatedAt = expectedDate,
            deletedAt = expectedDate
        )

        val actualOutput = useCase.execute(input)

        // then
        verify {
            genreGateway.save(
                withArg {
                    assertEquals(expectedId, it.id)
                    assertEquals(expectedName, it.name)
                    assertEquals(expectedIsActive, it.isActive)
                    assertEquals(expectedCategories, it.categories)
                    assertEquals(expectedDate, it.createdAt)
                    assertEquals(expectedDate, it.updatedAt)
                    assertEquals(expectedDate, it.deletedAt)
                }
            )
        }

        assertNotNull(actualOutput)
        assertEquals(expectedId, actualOutput.id)
    }

    @Test
    fun givenAnInvalidId_whenCallsSave_shouldReturnError() {
        // given
        val expectedId = ""
        val expectedName = "Business"
        val expectedIsActive = true
        val expectedCategories = setOf(IdUtils.uuid(), IdUtils.uuid())
        val expectedDate = InstantUtils.now()
        val expectedErrorCount = 1
        val expectedErrorMessage = "'id' should not be empty"

        // when
        val input = SaveGenreUseCase.Input(
            id = expectedId,
            name = expectedName,
            isActive = expectedIsActive,
            categories = expectedCategories,
            createdAt = expectedDate,
            updatedAt = expectedDate
        )

        val actualError = assertThrows<DomainException> { useCase.execute(input) }

        // then
        assertEquals(expectedErrorCount, actualError.errors.size)
        assertEquals(expectedErrorMessage, actualError.errors.first().message)

        verify { genreGateway.save(any()) wasNot Called }
    }

    @Test
    fun givenAnInvalidName_whenCallsSave_shouldReturnError() {
        // given
        val expectedId = IdUtils.uuid()
        val expectedName = " "
        val expectedIsActive = true
        val expectedCategories = setOf(IdUtils.uuid(), IdUtils.uuid())
        val expectedDate = InstantUtils.now()
        val expectedErrorCount = 1
        val expectedErrorMessage = "'name' should not be empty"

        // when
        val input = SaveGenreUseCase.Input(
            id = expectedId,
            name = expectedName,
            isActive = expectedIsActive,
            categories = expectedCategories,
            createdAt = expectedDate,
            updatedAt = expectedDate,
            deletedAt = expectedDate
        )

        val actualError = assertThrows<DomainException> { useCase.execute(input) }

        // then
        assertEquals(expectedErrorCount, actualError.errors.size)
        assertEquals(expectedErrorMessage, actualError.errors.first().message)

        verify { genreGateway.save(any()) wasNot Called }
    }
}
