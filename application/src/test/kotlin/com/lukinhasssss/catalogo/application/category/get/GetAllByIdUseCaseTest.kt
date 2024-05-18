package com.lukinhasssss.catalogo.application.category.get

import com.lukinhasssss.catalogo.application.UseCaseTest
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.domain.category.CategoryGateway
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class GetAllByIdUseCaseTest : UseCaseTest() {

    @InjectMockKs
    private lateinit var useCase: GetAllByIdUseCase

    @MockK
    private lateinit var categoryGateway: CategoryGateway

    @Test
    fun givenValidIds_whenCallsGetAllById_shouldReturnIt() {
        // given
        val categories = listOf(Fixture.Categories.aulas, Fixture.Categories.lives)

        val expectedItems = categories.map { GetAllByIdUseCase.Output(it) }

        val expectedIds = categories.map { it.id }.toSet()

        every { categoryGateway.findAllById(any()) } returns categories

        // when
        val actualOutput = useCase.execute(GetAllByIdUseCase.Input(expectedIds))

        // then
        assertTrue { expectedItems.size == actualOutput.size && expectedItems.containsAll(actualOutput) }

        verify { categoryGateway.findAllById(expectedIds) }
    }

    @Test
    fun givenInvalidIds_whenCallsGetAllById_shouldReturnEmpty() {
        // given
        val ids = setOf("1", "2", "3")

        every { categoryGateway.findAllById(any()) } returns emptyList()

        // when
        val actualOutput = useCase.execute(GetAllByIdUseCase.Input(ids))

        // then
        assertTrue { actualOutput.isEmpty() }

        verify { categoryGateway.findAllById(ids) }
    }

    @Test
    fun givenEmptyIds_whenCallsGetAllById_shouldReturnEmpty() {
        // given
        val ids = emptySet<String>()

        // when
        val actualOutput = useCase.execute(GetAllByIdUseCase.Input(ids))

        // then
        assertTrue { actualOutput.isEmpty() }

        verify(exactly = 0) { categoryGateway.findAllById(any()) }
    }
}
