package com.lukinhasssss.catalogo.application.genre.delete

import com.lukinhasssss.catalogo.application.UseCaseTest
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.domain.genre.GenreGateway
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class DeleteGenreUseCaseTest : UseCaseTest() {

    @InjectMockKs
    private lateinit var useCase: DeleteGenreUseCase

    @MockK
    private lateinit var genreGateway: GenreGateway

    @Test
    fun givenAValidId_whenCallsDelete_shouldBeOk() {
        // given
        val genre = Fixture.Genres.tech()

        val expectedId = genre.id

        every { genreGateway.deleteById(any()) } just Runs

        // when
        assertDoesNotThrow { useCase.execute(DeleteGenreUseCase.Input(expectedId)) }

        // then
        verify { genreGateway.deleteById(expectedId) }
    }

    @Test
    fun givenAnInvalidId_whenCallsDelete_shouldBeOk() {
        // given
        val expectedId = ""

        // when
        assertDoesNotThrow { useCase.execute(DeleteGenreUseCase.Input(expectedId)) }

        // then
        verify(exactly = 0) { genreGateway.deleteById(expectedId) }
    }
}
