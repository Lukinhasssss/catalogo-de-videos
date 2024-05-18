package com.lukinhasssss.catalogo.application.video.delete

import com.lukinhasssss.catalogo.application.UseCaseTest
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.domain.video.VideoGateway
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class DeleteVideoUseCaseTest : UseCaseTest() {

    @InjectMockKs
    private lateinit var useCase: DeleteVideoUseCase

    @MockK
    private lateinit var videoGateway: VideoGateway

    @Test
    fun givenAValidId_whenCallsDelete_shouldBeOk() {
        // given
        val video = Fixture.Videos.cleanCode()

        val expectedId = video.id

        every { videoGateway.deleteById(any()) } just Runs

        // when
        assertDoesNotThrow { useCase.execute(DeleteVideoUseCase.Input(expectedId)) }

        // then
        verify { videoGateway.deleteById(expectedId) }
    }

    @Test
    fun givenAnInvalidId_whenCallsDelete_shouldBeOk() {
        // given
        val expectedId = ""

        // when
        assertDoesNotThrow { useCase.execute(DeleteVideoUseCase.Input(expectedId)) }

        // then
        verify(exactly = 0) { videoGateway.deleteById(expectedId) }
    }
}
