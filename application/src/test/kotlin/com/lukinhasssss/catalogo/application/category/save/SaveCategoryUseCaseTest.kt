package com.lukinhasssss.catalogo.application.category.save

import com.lukinhasssss.catalogo.application.UseCaseTest
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.domain.category.Category
import com.lukinhasssss.catalogo.domain.category.CategoryGateway
import com.lukinhasssss.catalogo.domain.exception.DomainException
import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class SaveCategoryUseCaseTest : UseCaseTest() {

    @InjectMockKs
    private lateinit var useCase: SaveCategoryUseCase

    @MockK
    private lateinit var categoryGateway: CategoryGateway

    @Test
    fun givenAValidCategory_whenCallsSave_shouldPersistIt() {
        // given
        val aCategory = Fixture.Categories.aulas

        every { categoryGateway.save(any()) } answers { firstArg() }

        // when
        useCase.execute(aCategory)

        // then
        verify { categoryGateway.save(aCategory) }
    }

    @Test
    fun givenANullCategory_whenCallsSave_shouldReturnError() {
        // given
        val aCategory: Category? = null
        val expectedErrorCount = 1
        val expectedErrorMessage = "A category cannot be null"

        // when
        val actualError = assertThrows<DomainException> { useCase.execute(aCategory) }

        // then
        assertEquals(expectedErrorCount, actualError.errors.size)
        assertEquals(expectedErrorMessage, actualError.errors.first().message)

        verify { categoryGateway.save(any()) wasNot Called }
    }

    @Test
    fun givenAnInvalidId_whenCallsSave_shouldReturnError() {
        // given
        val expectedErrorCount = 1
        val expectedErrorMessage = "'id' should not be empty"

        val aCategory = Fixture.Categories.aulas.copy(id = "")

        // when
        val actualError = assertThrows<DomainException> { useCase.execute(aCategory) }

        // then
        assertEquals(expectedErrorCount, actualError.errors.size)
        assertEquals(expectedErrorMessage, actualError.errors.first().message)

        verify { categoryGateway.save(any()) wasNot Called }
    }

    @Test
    fun givenAnInvalidName_whenCallsSave_shouldReturnError() {
        // given
        val expectedErrorCount = 1
        val expectedErrorMessage = "'name' should not be empty"

        val aCategory = Fixture.Categories.aulas.copy(name = "")

        // when
        val actualError = assertThrows<DomainException> { useCase.execute(aCategory) }

        // then
        assertEquals(expectedErrorCount, actualError.errors.size)
        assertEquals(expectedErrorMessage, actualError.errors.first().message)

        verify { categoryGateway.save(any()) wasNot Called }
    }
}
