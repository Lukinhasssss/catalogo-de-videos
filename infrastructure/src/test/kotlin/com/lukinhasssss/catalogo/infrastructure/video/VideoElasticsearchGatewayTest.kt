package com.lukinhasssss.catalogo.infrastructure.video

import com.lukinhasssss.catalogo.AbstractElasticsearchTest
import com.lukinhasssss.catalogo.domain.utils.IdUtils
import com.lukinhasssss.catalogo.domain.utils.InstantUtils
import com.lukinhasssss.catalogo.domain.video.Rating
import com.lukinhasssss.catalogo.domain.video.Video
import com.lukinhasssss.catalogo.infrastructure.video.persistence.VideoRepository
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.Year
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class VideoElasticsearchGatewayTest : AbstractElasticsearchTest() {

    @Autowired
    private lateinit var videoGateway: VideoElasticsearchGateway

    @Autowired
    private lateinit var videoRepository: VideoRepository

    @Test
    fun testInjection() {
        assertNotNull(videoGateway)
        assertNotNull(videoRepository)
    }

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

        val input = Video.with(
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

        assertEquals(0, videoRepository.count())

        // when
        val actualOutput = videoGateway.save(input)

        // then
        assertEquals(1, videoRepository.count())

        assertEquals(input, actualOutput)

        with(videoRepository.findById(expectedId).get()) {
            assertEquals(expectedId, id)
            assertEquals(expectedTitle, title)
            assertEquals(expectedDescription, description)
            assertEquals(expectedLaunchedAt.value, launchedAt)
            assertEquals(expectedDuration, duration)
            assertEquals(expectedRating.name, rating)
            assertEquals(expectedOpened, opened)
            assertEquals(expectedPublished, published)
            assertEquals(expectedBanner, banner)
            assertEquals(expectedThumbnail, thumbnail)
            assertEquals(expectedThumbnailHalf, thumbnailHalf)
            assertEquals(expectedTrailer, trailer)
            assertEquals(expectedVideo, video)
            assertEquals(expectedCategories, categories)
            assertEquals(expectedCastMembers, castMembers)
            assertEquals(expectedGenres, genres)
            assertEquals(expectedCreatedAt.toString(), createdAt)
            assertEquals(expectedUpdatedAt.toString(), updatedAt)
        }
    }

    @Test
    fun givenMinimumInput_whenCallsSave_shouldPersistIt() {
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

        val input = Video.with(
            id = expectedId,
            title = expectedTitle,
            description = expectedDescription,
            launchedAt = expectedLaunchedAt.value,
            duration = expectedDuration,
            rating = expectedRating.name,
            opened = expectedOpened,
            published = expectedPublished,
            createdAt = expectedCreatedAt.toString(),
            updatedAt = expectedUpdatedAt.toString()
        )

        assertEquals(0, videoRepository.count())

        // when
        val actualOutput = videoGateway.save(input)

        // then
        assertEquals(1, videoRepository.count())

        assertEquals(input, actualOutput)

        with(videoRepository.findById(expectedId).get()) {
            assertEquals(expectedId, id)
            assertEquals(expectedTitle, title)
            assertEquals(expectedDescription, description)
            assertEquals(expectedLaunchedAt.value, launchedAt)
            assertEquals(expectedDuration, duration)
            assertEquals(expectedRating.name, rating)
            assertEquals(expectedOpened, opened)
            assertEquals(expectedPublished, published)
            assertEquals(expectedCreatedAt.toString(), createdAt)
            assertEquals(expectedUpdatedAt.toString(), updatedAt)
        }
    }
}
