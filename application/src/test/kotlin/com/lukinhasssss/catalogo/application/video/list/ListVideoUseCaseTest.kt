package com.lukinhasssss.catalogo.application.video.list

import com.lukinhasssss.catalogo.application.UseCaseTest
import com.lukinhasssss.catalogo.domain.Fixture.Videos.cleanCode
import com.lukinhasssss.catalogo.domain.Fixture.Videos.systemDesign
import com.lukinhasssss.catalogo.domain.pagination.Pagination
import com.lukinhasssss.catalogo.domain.video.VideoGateway
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ListVideoUseCaseTest : UseCaseTest() {

    @InjectMockKs
    private lateinit var useCase: ListVideoUseCase

    @MockK
    private lateinit var videoGateway: VideoGateway

    @Test
    fun givenAValidQuery_whenCallsListVideos_shouldReturnIt() {
        // given
        val videos = listOf(systemDesign(), cleanCode())

        val expectedItems = videos.map { ListVideoUseCase.Output.from(it) }

        val expectedPage = 0
        val expectedPerPage = 10
        val expectedTerms = "Algo"
        val expectedSort = "name"
        val expectedDirection = "asc"
        val expectedItemsCount = 2
        val expectedYear = 2024
        val expectedRating = "L"
        val expectedCategories = setOf("c1")
        val expectedCastMembers = setOf("cm1")
        val expectedGenres = setOf("g1")

        val aQuery = ListVideoUseCase.Input(
            page = expectedPage,
            perPage = expectedPerPage,
            terms = expectedTerms,
            sort = expectedSort,
            direction = expectedDirection,
            launchedAt = expectedYear,
            rating = expectedRating,
            categories = expectedCategories,
            castMembers = expectedCastMembers,
            genres = expectedGenres
        )

        val pagination = Pagination(
            currentPage = expectedPage,
            perPage = expectedPerPage,
            total = videos.size.toLong(),
            items = videos
        )

        every { videoGateway.findAll(any()) } returns pagination

        // when
        val actualOutput = useCase.execute(aQuery)

        // then
        with(actualOutput) {
            assertEquals(expectedPage, meta.currentPage)
            assertEquals(expectedPerPage, meta.perPage)
            assertEquals(expectedItemsCount, meta.total.toInt())
            assertTrue(expectedItems.size == data.size && expectedItems.containsAll(data))
        }
    }
}
