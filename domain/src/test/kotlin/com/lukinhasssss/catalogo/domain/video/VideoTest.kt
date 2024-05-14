package com.lukinhasssss.catalogo.domain.video

import com.lukinhasssss.catalogo.domain.UnitTest
import com.lukinhasssss.catalogo.domain.exception.DomainException
import com.lukinhasssss.catalogo.domain.utils.IdUtils
import com.lukinhasssss.catalogo.domain.utils.InstantUtils
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
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

    @Test
    fun givenEmptyBanner_whenCallsVideoWith_shouldOverwritePublishedToFalse() {
        // given
        val actualPublished = true
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
        val expectedBanner = ""
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
            launchedAt = expectedLaunchedAt.value,
            duration = expectedDuration,
            rating = expectedRating.name,
            opened = expectedOpened,
            published = actualPublished,
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
    fun givenEmptyThumbnail_whenCallsVideoWith_shouldOverwritePublishedToFalse() {
        // given
        val actualPublished = true
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
        val expectedThumbnail = ""
        val expectedThumbnailHalf = "http://thumbnailHalf.com"
        val expectedCategories = setOf(IdUtils.uuid())
        val expectedGenres = setOf(IdUtils.uuid())
        val expectedCastMembers = setOf(IdUtils.uuid())

        // when
        val actualVideo = Video.with(
            id = expectedId,
            title = expectedTitle,
            description = expectedDescription,
            launchedAt = expectedLaunchedAt.value,
            duration = expectedDuration,
            rating = expectedRating.name,
            opened = expectedOpened,
            published = actualPublished,
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
    fun givenEmptyThumbnailHalf_whenCallsVideoWith_shouldOverwritePublishedToFalse() {
        // given
        val actualPublished = true
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
        val expectedThumbnailHalf = ""
        val expectedCategories = setOf(IdUtils.uuid())
        val expectedGenres = setOf(IdUtils.uuid())
        val expectedCastMembers = setOf(IdUtils.uuid())

        // when
        val actualVideo = Video.with(
            id = expectedId,
            title = expectedTitle,
            description = expectedDescription,
            launchedAt = expectedLaunchedAt.value,
            duration = expectedDuration,
            rating = expectedRating.name,
            opened = expectedOpened,
            published = actualPublished,
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
    fun givenEmptyTrailer_whenCallsVideoWith_shouldOverwritePublishedToFalse() {
        // given
        val actualPublished = true
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
        val expectedTrailer = ""
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
            launchedAt = expectedLaunchedAt.value,
            duration = expectedDuration,
            rating = expectedRating.name,
            opened = expectedOpened,
            published = actualPublished,
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
    fun givenEmptyVideo_whenCallsVideoWith_shouldOverwritePublishedToFalse() {
        // given
        val actualPublished = true
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
        val expectedVideo = ""
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
            launchedAt = expectedLaunchedAt.value,
            duration = expectedDuration,
            rating = expectedRating.name,
            opened = expectedOpened,
            published = actualPublished,
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
    fun givenAnInvalidId_whenCallsWith_thenShouldReceiveError() {
        // given
        val expectedErrorCount = 1
        val expectedErrorMessage = "'id' should not be empty"

        val expectedId = " "
        val expectedTitle = "Video Title"
        val expectedDescription = "Video Description"
        val expectedLaunchedAt = Year.of(2022)
        val expectedDuration = 120.10
        val expectedOpened = false
        val expectedPublished = true
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
        val actualException = assertThrows<DomainException> {
            Video.with(
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
        }

        // then
        with(actualException.errors) {
            assertEquals(expectedErrorCount, count())
            assertEquals(expectedErrorMessage, first().message)
        }
    }

    @Test
    fun givenAnInvalidTitle_whenCallsWith_thenShouldReceiveError() {
        // given
        val expectedErrorCount = 1
        val expectedErrorMessage = "'title' should not be empty"

        val expectedId = IdUtils.uuid()
        val expectedTitle = " "
        val expectedDescription = "Video Description"
        val expectedLaunchedAt = Year.of(2022)
        val expectedDuration = 120.10
        val expectedOpened = false
        val expectedPublished = true
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
        val actualException = assertThrows<DomainException> {
            Video.with(
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
        }

        // then
        with(actualException.errors) {
            assertEquals(expectedErrorCount, count())
            assertEquals(expectedErrorMessage, first().message)
        }
    }

    @Test
    fun givenAnInvalidDescription_whenCallsWith_thenShouldReceiveError() {
        // given
        val expectedErrorCount = 1
        val expectedErrorMessage = "'description' should not be empty"

        val expectedId = IdUtils.uuid()
        val expectedTitle = "Video Title"
        val expectedDescription = " "
        val expectedLaunchedAt = Year.of(2022)
        val expectedDuration = 120.10
        val expectedOpened = false
        val expectedPublished = true
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
        val actualException = assertThrows<DomainException> {
            Video.with(
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
        }

        // then
        with(actualException.errors) {
            assertEquals(expectedErrorCount, count())
            assertEquals(expectedErrorMessage, first().message)
        }
    }
}
