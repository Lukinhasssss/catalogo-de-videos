package com.lukinhasssss.catalogo.application.video.save

import com.lukinhasssss.catalogo.application.UseCaseTest
import com.lukinhasssss.catalogo.domain.exception.DomainException
import com.lukinhasssss.catalogo.domain.utils.IdUtils
import com.lukinhasssss.catalogo.domain.utils.InstantUtils
import com.lukinhasssss.catalogo.domain.video.Rating
import com.lukinhasssss.catalogo.domain.video.VideoGateway
import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Year
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class SaveVideoUseCaseTest : UseCaseTest() {

    @InjectMockKs
    private lateinit var useCase: SaveVideoUseCase

    @MockK
    private lateinit var videoGateway: VideoGateway

    @Test
    fun givenAValidInput_whenCallsSave_shouldPersistIt() {
        // given
        val expectedId = IdUtils.uuid()
        val expectedTitle = "Video Title"
        val expectedDescription = "Video Description"
        val expectedLaunchedAt = Year.of(2022)
        val expectedDuration = 120.10
        val expectedOpened = false
        val expectedPublished = false
        val expectedRating = Rating.L
        val expectedCreatedAt = InstantUtils.now()
        val expectedUpdatedAt = InstantUtils.now()
        val expectedVideo = "http://video.com"
        val expectedTrailer = "http://trailer.com"
        val expectedBanner = "http://banner.com"
        val expectedThumbnail = "http://thumbnail.com"
        val expectedThumbnailHalf = "http://thumbnailHalf.com"
        val expectedCategories = setOf(IdUtils.uuid())
        val expectedGenres = setOf(IdUtils.uuid())
        val expectedCastMembers = setOf(IdUtils.uuid())

        val input = SaveVideoUseCase.Input(
            id = expectedId,
            title = expectedTitle,
            description = expectedDescription,
            launchedAt = expectedLaunchedAt.value,
            duration = expectedDuration,
            rating = expectedRating.name,
            opened = expectedOpened,
            published = expectedPublished,
            createdAt = expectedCreatedAt.toString(),
            updatedAt = expectedUpdatedAt.toString(),
            banner = expectedBanner,
            thumbnail = expectedThumbnail,
            thumbnailHalf = expectedThumbnailHalf,
            trailer = expectedTrailer,
            video = expectedVideo,
            categories = expectedCategories,
            genres = expectedGenres,
            castMembers = expectedCastMembers
        )

        every { videoGateway.save(any()) } answers { firstArg() }

        // when
        val actualOutput = useCase.execute(input)

        // then
        assertNotNull(actualOutput)
        assertEquals(expectedId, actualOutput.id)

        verify {
            videoGateway.save(
                withArg {
                    assertNotNull(it)
                    assertEquals(expectedId, it.id)
                    assertEquals(expectedTitle, it.title)
                    assertEquals(expectedDescription, it.description)
                    assertEquals(expectedLaunchedAt, it.launchedAt)
                    assertEquals(expectedDuration, it.duration)
                    assertEquals(expectedOpened, it.opened)
                    assertEquals(expectedPublished, it.published)
                    assertEquals(expectedRating, it.rating)
                    assertEquals(expectedCreatedAt, it.createdAt)
                    assertEquals(expectedUpdatedAt, it.updatedAt)
                    assertEquals(expectedCategories, it.categories)
                    assertEquals(expectedGenres, it.genres)
                    assertEquals(expectedCastMembers, it.castMembers)
                    assertEquals(expectedVideo, it.video)
                    assertEquals(expectedTrailer, it.trailer)
                    assertEquals(expectedBanner, it.banner)
                    assertEquals(expectedThumbnail, it.thumbnail)
                    assertEquals(expectedThumbnailHalf, it.thumbnailHalf)
                }
            )
        }
    }

    @Test
    fun givenAnInvalidId_whenCallsSave_shouldReturnError() {
        // given
        val expectedErrorCount = 1
        val expectedErrorMessage = "'id' should not be empty"

        val expectedId = " "
        val expectedTitle = "Video Title"
        val expectedDescription = "Video Description"
        val expectedLaunchedAt = Year.of(2022)
        val expectedDuration = 120.10
        val expectedOpened = false
        val expectedPublished = false
        val expectedRating = Rating.L
        val expectedCreatedAt = InstantUtils.now()
        val expectedUpdatedAt = InstantUtils.now()
        val expectedVideo = "http://video.com"
        val expectedTrailer = "http://trailer.com"
        val expectedBanner = "http://banner.com"
        val expectedThumbnail = "http://thumbnail.com"
        val expectedThumbnailHalf = "http://thumbnailHalf.com"
        val expectedCategories = setOf(IdUtils.uuid())
        val expectedGenres = setOf(IdUtils.uuid())
        val expectedCastMembers = setOf(IdUtils.uuid())

        val input = SaveVideoUseCase.Input(
            id = expectedId,
            title = expectedTitle,
            description = expectedDescription,
            launchedAt = expectedLaunchedAt.value,
            duration = expectedDuration,
            rating = expectedRating.name,
            opened = expectedOpened,
            published = expectedPublished,
            createdAt = expectedCreatedAt.toString(),
            updatedAt = expectedUpdatedAt.toString(),
            banner = expectedBanner,
            thumbnail = expectedThumbnail,
            thumbnailHalf = expectedThumbnailHalf,
            trailer = expectedTrailer,
            video = expectedVideo,
            categories = expectedCategories,
            genres = expectedGenres,
            castMembers = expectedCastMembers
        )

        // when

        val actualError = assertThrows<DomainException> { useCase.execute(input) }

        // then
        assertEquals(expectedErrorCount, actualError.errors.size)
        assertEquals(expectedErrorMessage, actualError.errors.first().message)

        verify { videoGateway.save(any()) wasNot Called }
    }

    @Test
    fun givenAnInvalidTitle_whenCallsSave_shouldReturnError() {
        // given
        val expectedErrorCount = 1
        val expectedErrorMessage = "'title' should not be empty"

        val expectedId = IdUtils.uuid()
        val expectedTitle = " "
        val expectedDescription = "Video Description"
        val expectedLaunchedAt = Year.of(2022)
        val expectedDuration = 120.10
        val expectedOpened = false
        val expectedPublished = false
        val expectedRating = Rating.L
        val expectedCreatedAt = InstantUtils.now()
        val expectedUpdatedAt = InstantUtils.now()
        val expectedVideo = "http://video.com"
        val expectedTrailer = "http://trailer.com"
        val expectedBanner = "http://banner.com"
        val expectedThumbnail = "http://thumbnail.com"
        val expectedThumbnailHalf = "http://thumbnailHalf.com"
        val expectedCategories = setOf(IdUtils.uuid())
        val expectedGenres = setOf(IdUtils.uuid())
        val expectedCastMembers = setOf(IdUtils.uuid())

        val input = SaveVideoUseCase.Input(
            id = expectedId,
            title = expectedTitle,
            description = expectedDescription,
            launchedAt = expectedLaunchedAt.value,
            duration = expectedDuration,
            rating = expectedRating.name,
            opened = expectedOpened,
            published = expectedPublished,
            createdAt = expectedCreatedAt.toString(),
            updatedAt = expectedUpdatedAt.toString(),
            banner = expectedBanner,
            thumbnail = expectedThumbnail,
            thumbnailHalf = expectedThumbnailHalf,
            trailer = expectedTrailer,
            video = expectedVideo,
            categories = expectedCategories,
            genres = expectedGenres,
            castMembers = expectedCastMembers
        )

        // when

        val actualError = assertThrows<DomainException> { useCase.execute(input) }

        // then
        assertEquals(expectedErrorCount, actualError.errors.size)
        assertEquals(expectedErrorMessage, actualError.errors.first().message)

        verify { videoGateway.save(any()) wasNot Called }
    }

    @Test
    fun givenAnInvalidDescription_whenCallsSave_shouldReturnError() {
        // given
        val expectedErrorCount = 1
        val expectedErrorMessage = "'description' should not be empty"

        val expectedId = IdUtils.uuid()
        val expectedTitle = "Video Title"
        val expectedDescription = " "
        val expectedLaunchedAt = Year.of(2022)
        val expectedDuration = 120.10
        val expectedOpened = false
        val expectedPublished = false
        val expectedRating = Rating.L
        val expectedCreatedAt = InstantUtils.now()
        val expectedUpdatedAt = InstantUtils.now()
        val expectedVideo = "http://video.com"
        val expectedTrailer = "http://trailer.com"
        val expectedBanner = "http://banner.com"
        val expectedThumbnail = "http://thumbnail.com"
        val expectedThumbnailHalf = "http://thumbnailHalf.com"
        val expectedCategories = setOf(IdUtils.uuid())
        val expectedGenres = setOf(IdUtils.uuid())
        val expectedCastMembers = setOf(IdUtils.uuid())

        val input = SaveVideoUseCase.Input(
            id = expectedId,
            title = expectedTitle,
            description = expectedDescription,
            launchedAt = expectedLaunchedAt.value,
            duration = expectedDuration,
            rating = expectedRating.name,
            opened = expectedOpened,
            published = expectedPublished,
            createdAt = expectedCreatedAt.toString(),
            updatedAt = expectedUpdatedAt.toString(),
            banner = expectedBanner,
            thumbnail = expectedThumbnail,
            thumbnailHalf = expectedThumbnailHalf,
            trailer = expectedTrailer,
            video = expectedVideo,
            categories = expectedCategories,
            genres = expectedGenres,
            castMembers = expectedCastMembers
        )

        // when

        val actualError = assertThrows<DomainException> { useCase.execute(input) }

        // then
        assertEquals(expectedErrorCount, actualError.errors.size)
        assertEquals(expectedErrorMessage, actualError.errors.first().message)

        verify { videoGateway.save(any()) wasNot Called }
    }
}
