package com.lukinhasssss.catalogo.application.video.get

import com.lukinhasssss.catalogo.application.UseCaseTest
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.domain.video.VideoGateway
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class GetVideoUseCaseTest : UseCaseTest() {

    @InjectMockKs
    private lateinit var useCase: GetVideoUseCase

    @MockK
    private lateinit var videoGateway: VideoGateway

    @Test
    fun givenAValidVideo_whenCallsGet_shouldReturnIt() {
        // Given
        val cleanCode = Fixture.Videos.cleanCode()

        every { videoGateway.findById(any()) } returns cleanCode

        // When
        val actualOutput = useCase.execute(GetVideoUseCase.Input(cleanCode.id))

        // Then
        with(actualOutput) {
            assertNotNull(this)
            assertEquals(cleanCode.id, id)
            assertEquals(cleanCode.title, title)
            assertEquals(cleanCode.description, description)
            assertEquals(cleanCode.launchedAt.value, launchedAt)
            assertEquals(cleanCode.duration, duration)
            assertEquals(cleanCode.opened, opened)
            assertEquals(cleanCode.published, published)
            assertEquals(cleanCode.rating.name, rating)
            assertEquals(cleanCode.createdAt.toString(), createdAt)
            assertEquals(cleanCode.updatedAt.toString(), updatedAt)
            assertEquals(cleanCode.categories, categories)
            assertEquals(cleanCode.genres, genres)
            assertEquals(cleanCode.castMembers, castMembers)
            assertEquals(cleanCode.banner, banner)
            assertEquals(cleanCode.thumbnail, thumbnail)
            assertEquals(cleanCode.thumbnailHalf, thumbnailHalf)
            assertEquals(cleanCode.trailer, trailer)
            assertEquals(cleanCode.video, video)
        }

        verify { videoGateway.findById(cleanCode.id) }
    }

    @Test
    fun givenAnInvalidId_whenCallsGet_shouldShouldReturnNull() {
        // given
        val expectedId = " "

        // when
        val actualOutput = assertDoesNotThrow { useCase.execute(GetVideoUseCase.Input(expectedId)) }

        // then
        assertNull(actualOutput)

        verify(exactly = 0) { videoGateway.findById(any()) }
    }
}
