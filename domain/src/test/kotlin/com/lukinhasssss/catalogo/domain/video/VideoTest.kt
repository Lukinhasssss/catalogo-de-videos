package com.lukinhasssss.catalogo.domain.video

import com.lukinhasssss.catalogo.domain.UnitTest
import com.lukinhasssss.catalogo.domain.utils.IdUtils
import com.lukinhasssss.catalogo.domain.utils.InstantUtils
import org.junit.jupiter.api.Test
import java.time.Year
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class VideoTest : UnitTest() {

    @Test
    fun givenValidParams_whenCallWith_shouldInstantiate() {
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

        // when
        val actualVideo = Video.with(
            id = expectedId,
            title = expectedTitle,
            description = expectedDescription,
            launchedAt = expectedLaunchedAt,
            duration = expectedDuration,
            rating = expectedRating,
            opened = expectedOpened,
            published = expectedPublished,
            createdAt = expectedCreatedAt,
            updatedAt = expectedUpdatedAt,
            banner = expectedBanner,
            thumbnail = expectedThumbnail,
            thumbnailHalf = expectedThumbnailHalf,
            trailer = expectedTrailer,
            video = expectedVideo,
            categories = expectedCategories,
            genres = expectedGenres,
            castMembers = expectedCastMembers
        )

        // then
        with(actualVideo) {
            assertNotNull(this)
            assertEquals(expectedId, id)
            assertEquals(expectedTitle, title)
            assertEquals(expectedDescription, description)
            assertEquals(expectedLaunchedAt, launchedAt)
            assertEquals(expectedDuration, duration)
            assertEquals(expectedOpened, opened)
            assertEquals(expectedPublished, published)
            assertEquals(expectedRating, rating)
            assertEquals(expectedCreatedAt, createdAt)
            assertEquals(expectedUpdatedAt, updatedAt)
            assertEquals(expectedCategories, categories)
            assertEquals(expectedGenres, genres)
            assertEquals(expectedCastMembers, castMembers)
            assertEquals(expectedVideo, video)
            assertEquals(expectedTrailer, trailer)
            assertEquals(expectedBanner, banner)
            assertEquals(expectedThumbnail, thumbnail)
            assertEquals(expectedThumbnailHalf, thumbnailHalf)
        }
    }

    @Test
    fun givenAValidVideo_whenCallWith_shouldInstantiate() {
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

        val video = Video.with(
            id = expectedId,
            title = expectedTitle,
            description = expectedDescription,
            launchedAt = expectedLaunchedAt,
            duration = expectedDuration,
            rating = expectedRating,
            opened = expectedOpened,
            published = expectedPublished,
            createdAt = expectedCreatedAt,
            updatedAt = expectedUpdatedAt,
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
        val actualVideo = Video.with(video)

        // then
        with(actualVideo) {
            assertNotNull(this)
            assertEquals(expectedId, id)
            assertEquals(expectedTitle, title)
            assertEquals(expectedDescription, description)
            assertEquals(expectedLaunchedAt, launchedAt)
            assertEquals(expectedDuration, duration)
            assertEquals(expectedOpened, opened)
            assertEquals(expectedPublished, published)
            assertEquals(expectedRating, rating)
            assertEquals(expectedCreatedAt, createdAt)
            assertEquals(expectedUpdatedAt, updatedAt)
            assertEquals(expectedCategories, categories)
            assertEquals(expectedGenres, genres)
            assertEquals(expectedCastMembers, castMembers)
            assertEquals(expectedVideo, this.video)
            assertEquals(expectedTrailer, trailer)
            assertEquals(expectedBanner, banner)
            assertEquals(expectedThumbnail, thumbnail)
            assertEquals(expectedThumbnailHalf, thumbnailHalf)
        }
    }
}
