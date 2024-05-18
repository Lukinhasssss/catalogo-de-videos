package com.lukinhasssss.catalogo.application.genre.get

import com.lukinhasssss.catalogo.application.UseCaseTest
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.domain.genre.GenreGateway
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
    private lateinit var genreGateway: GenreGateway

    @Test
    fun givenValidIds_whenCallsGetAllById_shouldReturnIt() {
        // given
        val genres = listOf(Fixture.Genres.tech(), Fixture.Genres.business())

        val expectedItems = genres.map { GetAllByIdUseCase.Output(it) }

        val expectedIds = genres.map { it.id }

        every { genreGateway.findAllById(any()) } returns genres

        // when
        val actualOutput = useCase.execute(GetAllByIdUseCase.Input(expectedIds))

        // then
        assertTrue { expectedItems.size == actualOutput.size && expectedItems.containsAll(actualOutput) }

        verify { genreGateway.findAllById(expectedIds) }
    }

    @Test
    fun givenInvalidIds_whenCallsGetAllById_shouldReturnEmpty() {
        // given
        val ids = listOf("1", "2", "3")

        every { genreGateway.findAllById(any()) } returns emptyList()

        // when
        val actualOutput = useCase.execute(GetAllByIdUseCase.Input(ids))

        // then
        assertTrue { actualOutput.isEmpty() }

        verify { genreGateway.findAllById(ids) }
    }

    @Test
    fun givenEmptyIds_whenCallsGetAllById_shouldReturnEmpty() {
        // given
        val ids = emptyList<String>()

        // when
        val actualOutput = useCase.execute(GetAllByIdUseCase.Input(ids))

        // then
        assertTrue { actualOutput.isEmpty() }

        verify(exactly = 0) { genreGateway.findAllById(any()) }
    }
}
