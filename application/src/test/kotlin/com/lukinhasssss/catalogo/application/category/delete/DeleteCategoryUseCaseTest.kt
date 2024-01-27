package com.lukinhasssss.catalogo.application.category.delete

import com.lukinhasssss.catalogo.application.UseCaseTest
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.domain.category.CategoryGateway
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class DeleteCategoryUseCaseTest : UseCaseTest() {

    @InjectMockKs
    private lateinit var useCase: DeleteCategoryUseCase

    @MockK
    private lateinit var categoryGateway: CategoryGateway

    @Test
    fun givenAValidId_whenCallsDelete_shouldBeOk() {
        // given
        val aulas = Fixture.Categories.aulas

        val expectedId = aulas.id

        every { categoryGateway.deleteById(any()) } just Runs

        // when
        assertDoesNotThrow { useCase.execute(expectedId) }

        // then
        verify { categoryGateway.deleteById(expectedId) }
    }

    @Test
    fun givenAnInvalidId_whenCallsDelete_shouldBeOk() {
        // given
        val expectedId = ""

        // when
        assertDoesNotThrow { useCase.execute(expectedId) }

        // then
        verify(exactly = 0) { categoryGateway.deleteById(expectedId) }
    }
}
