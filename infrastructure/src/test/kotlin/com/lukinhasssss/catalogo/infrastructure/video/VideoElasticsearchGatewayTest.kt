package com.lukinhasssss.catalogo.infrastructure.video

import com.lukinhasssss.catalogo.AbstractElasticsearchTest
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.domain.utils.IdUtils
import com.lukinhasssss.catalogo.domain.utils.InstantUtils
import com.lukinhasssss.catalogo.domain.video.Rating
import com.lukinhasssss.catalogo.domain.video.Video
import com.lukinhasssss.catalogo.domain.video.VideoSearchQuery
import com.lukinhasssss.catalogo.infrastructure.video.persistence.VideoDocument
import com.lukinhasssss.catalogo.infrastructure.video.persistence.VideoRepository
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import java.time.Year
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

    @Test
    fun givenAValidId_whenCallsDeleteById_shouldDeleteIt() {
        // given
        val cleanCode = Fixture.Videos.cleanCode()

        videoRepository.save(VideoDocument.from(cleanCode))

        val expectedId = cleanCode.id

        assertTrue(videoRepository.existsById(expectedId))

        // when
        videoGateway.deleteById(expectedId)

        // then
        assertFalse(videoRepository.existsById(expectedId))
    }

    @Test
    fun givenAnInvalidId_whenCallsDeleteById_shouldBeOk() {
        // when/then
        assertDoesNotThrow { videoGateway.deleteById("any") }
    }

    @Test
    fun givenAnEmptyId_whenCallsDeleteById_shouldBeOk() {
        // when/then
        assertDoesNotThrow { videoGateway.deleteById("") }
    }

    @Test
    fun givenVideoPersisted_whenCallsFindById_shouldRetrieveIt() {
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

        videoRepository.save(
            VideoDocument(
                id = expectedId,
                title = expectedTitle,
                description = expectedDescription,
                launchedAt = expectedLaunchedAt.value,
                duration = expectedDuration,
                rating = expectedRating.name,
                opened = expectedOpened,
                published = expectedPublished,
                banner = expectedBanner,
                thumbnail = expectedThumbnail,
                thumbnailHalf = expectedThumbnailHalf,
                trailer = expectedTrailer,
                video = expectedVideo,
                categories = expectedCategories,
                castMembers = expectedCastMembers,
                genres = expectedGenres,
                createdAt = expectedCreatedAt.toString(),
                updatedAt = expectedUpdatedAt.toString()
            )
        )

        assertEquals(1, videoRepository.count())

        // when
        val actualOutput = videoGateway.findById(expectedId)

        // then
        assertEquals(1, videoRepository.count())

        with(actualOutput!!) {
            assertEquals(expectedId, id)
            assertEquals(expectedTitle, title)
            assertEquals(expectedDescription, description)
            assertEquals(expectedLaunchedAt, launchedAt)
            assertEquals(expectedDuration, duration)
            assertEquals(expectedRating, rating)
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
            assertEquals(expectedCreatedAt, createdAt)
            assertEquals(expectedUpdatedAt, updatedAt)
        }
    }

    @Test
    fun givenInvalidEmptyId_whenCallsFindById_shouldReturnNull() {
        // when
        val actualOutput = videoGateway.findById("")

        // then
        assertEquals(null, actualOutput)
    }

    @Test
    fun givenInvalidId_whenCallsFindById_shouldReturnNull() {
        // when
        val actualOutput = videoGateway.findById("any")

        // then
        assertEquals(null, actualOutput)
    }

    @Test
    fun givenEmptyVideos_whenCallsFindAll_shouldReturnEmptyList() {
        // given
        val expectedPage = 0
        val expectedPerPage = 10
        val expectedTerms = ""
        val expectedSort = "title"
        val expectedDirection = "asc"
        val expectedTotal = 0

        val aQuery = VideoSearchQuery(
            page = expectedPage,
            perPage = expectedPerPage,
            terms = expectedTerms,
            sort = expectedSort,
            direction = expectedDirection
        )

        // when
        val actualOutput = videoGateway.findAll(aQuery)

        // then
        with(actualOutput) {
            assertEquals(expectedPage, meta.currentPage)
            assertEquals(expectedPerPage, meta.perPage)
            assertEquals(expectedTotal.toLong(), meta.total)
            assertEquals(expectedTotal, data.size)
        }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "code, 0, 10, 1, 1, Clean Code",
            "gol, 0, 10, 1, 1, Golang",
            "design, 0, 10, 1, 1, System Design",
            "assistido, 0, 10, 1, 1, System Design",
            "limpo, 0, 10, 1, 1, Clean Code",
            "linguagem, 0, 10, 1, 1, Golang"
        ]
    )
    fun givenValidTerm_whenCallsFindAll_shouldReturnElementsFiltered(
        expectedTerms: String,
        expectedPage: Int,
        expectedPerPage: Int,
        expectedItemsCount: Int,
        expectedTotal: Long,
        expectedTitle: String
    ) {
        // given
        mockVideos()

        val expectedSort = "title"
        val expectedDirection = "asc"

        val aQuery = VideoSearchQuery(
            page = expectedPage,
            perPage = expectedPerPage,
            terms = expectedTerms,
            sort = expectedSort,
            direction = expectedDirection
        )

        // when
        val actualOutput = videoGateway.findAll(aQuery)

        // then
        with(actualOutput) {
            assertEquals(expectedPage, meta.currentPage)
            assertEquals(expectedPerPage, meta.perPage)
            assertEquals(expectedTotal, meta.total)
            assertEquals(expectedItemsCount, data.size)
            assertEquals(expectedTitle, data[0].title)
        }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "meeting, 0, 10, 1, 1, Golang",
            "aulas, 0, 10, 1, 1, System Design",
            "lives, 0, 10, 1, 1, Clean Code",
            ", 0, 10, 3, 3, Clean Code"
        ]
    )
    fun givenValidCategory_whenCallsFindAll_shouldReturnElementsFiltered(
        expectedCategories: String?,
        expectedPage: Int,
        expectedPerPage: Int,
        expectedItemsCount: Int,
        expectedTotal: Long,
        expectedName: String
    ) {
        // given
        mockVideos()

        val expectedTerms = ""
        val expectedSort = "title"
        val expectedDirection = "asc"

        val aQuery = VideoSearchQuery(
            page = expectedPage,
            perPage = expectedPerPage,
            terms = expectedTerms,
            sort = expectedSort,
            direction = expectedDirection,
            categories = if (expectedCategories != null) setOf(expectedCategories) else setOf()
        )

        // when
        val actualOutput = videoGateway.findAll(aQuery)

        // then
        with(actualOutput) {
            assertEquals(expectedPage, meta.currentPage)
            assertEquals(expectedPerPage, meta.perPage)
            assertEquals(expectedTotal, meta.total)
            assertEquals(expectedItemsCount, data.size)
            assertEquals(expectedName, data[0].title)
        }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "luffy, 0, 10, 1, 1, Golang",
            "nami, 0, 10, 1, 1, System Design",
            "zoro, 0, 10, 1, 1, Clean Code",
            ", 0, 10, 3, 3, Clean Code"
        ]
    )
    fun givenValidCastMember_whenCallsFindAll_shouldReturnElementsFiltered(
        expectedCastMember: String?,
        expectedPage: Int,
        expectedPerPage: Int,
        expectedItemsCount: Int,
        expectedTotal: Long,
        expectedName: String
    ) {
        // given
        mockVideos()

        val expectedTerms = ""
        val expectedSort = "title"
        val expectedDirection = "asc"

        val aQuery = VideoSearchQuery(
            page = expectedPage,
            perPage = expectedPerPage,
            terms = expectedTerms,
            sort = expectedSort,
            direction = expectedDirection,
            castMembers = if (expectedCastMember != null) setOf(expectedCastMember) else setOf()
        )

        // when
        val actualOutput = videoGateway.findAll(aQuery)

        // then
        with(actualOutput) {
            assertEquals(expectedPage, meta.currentPage)
            assertEquals(expectedPerPage, meta.perPage)
            assertEquals(expectedTotal, meta.total)
            assertEquals(expectedItemsCount, data.size)
            assertEquals(expectedName, data[0].title)
        }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "golang, 0, 10, 1, 1, Golang",
            "systemdesign, 0, 10, 1, 1, System Design",
            "cleancode, 0, 10, 1, 1, Clean Code",
            ", 0, 10, 3, 3, Clean Code"
        ]
    )
    fun givenValidGenre_whenCallsFindAll_shouldReturnElementsFiltered(
        expectedGenre: String?,
        expectedPage: Int,
        expectedPerPage: Int,
        expectedItemsCount: Int,
        expectedTotal: Long,
        expectedName: String
    ) {
        // given
        mockVideos()

        val expectedTerms = ""
        val expectedSort = "title"
        val expectedDirection = "asc"

        val aQuery = VideoSearchQuery(
            page = expectedPage,
            perPage = expectedPerPage,
            terms = expectedTerms,
            sort = expectedSort,
            direction = expectedDirection,
            genres = if (expectedGenre != null) setOf(expectedGenre) else setOf()
        )

        // when
        val actualOutput = videoGateway.findAll(aQuery)

        // then
        with(actualOutput) {
            assertEquals(expectedPage, meta.currentPage)
            assertEquals(expectedPerPage, meta.perPage)
            assertEquals(expectedTotal, meta.total)
            assertEquals(expectedItemsCount, data.size)
            assertEquals(expectedName, data[0].title)
        }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "title, asc, 0, 10, 3, 3, Clean Code",
            "title, desc, 0, 10, 3, 3, System Design",
            "created_at, asc, 0, 10, 3, 3, System Design",
            "created_at, desc, 0, 10, 3, 3, Golang"
        ]
    )
    fun givenValidSortAndDirection_whenCallsFindAll_shouldReturnElementsSorted(
        expectedSort: String,
        expectedDirection: String,
        expectedPage: Int,
        expectedPerPage: Int,
        expectedItemsCount: Int,
        expectedTotal: Long,
        expectedName: String
    ) {
        // given
        mockVideos()

        val expectedTerms = ""

        val aQuery = VideoSearchQuery(
            page = expectedPage,
            perPage = expectedPerPage,
            terms = expectedTerms,
            sort = expectedSort,
            direction = expectedDirection
        )

        // when
        val actualOutput = videoGateway.findAll(aQuery)

        // then
        with(actualOutput) {
            assertEquals(expectedPage, meta.currentPage)
            assertEquals(expectedPerPage, meta.perPage)
            assertEquals(expectedTotal, meta.total)
            assertEquals(expectedItemsCount, data.size)
            assertEquals(expectedName, data[0].title)
        }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "0, 1, 1, 3, Clean Code",
            "1, 1, 1, 3, Golang",
            "2, 1, 1, 3, System Design",
            "3, 1, 0, 3,"
        ]
    )
    fun givenValidPage_whenCallsFindAll_shouldReturnElementsPaged(
        expectedPage: Int,
        expectedPerPage: Int,
        expectedItemsCount: Int,
        expectedTotal: Long,
        expectedName: String?
    ) {
        // given
        mockVideos()

        val expectedTerms = ""
        val expectedSort = "title"
        val expectedDirection = "asc"

        val aQuery = VideoSearchQuery(
            page = expectedPage,
            perPage = expectedPerPage,
            terms = expectedTerms,
            sort = expectedSort,
            direction = expectedDirection
        )

        // when
        val actualOutput = videoGateway.findAll(aQuery)

        // then
        with(actualOutput) {
            assertEquals(expectedPage, meta.currentPage)
            assertEquals(expectedPerPage, meta.perPage)
            assertEquals(expectedTotal, meta.total)
            assertEquals(expectedItemsCount, data.size)

            if (expectedName != null) {
                assertEquals(expectedName, data[0].title)
            }
        }
    }

    private fun mockVideos() {
        videoRepository.save(VideoDocument.from(Fixture.Videos.systemDesign()))
        videoRepository.save(VideoDocument.from(Fixture.Videos.cleanCode()))
        videoRepository.save(VideoDocument.from(Fixture.Videos.golang()))
    }
}
