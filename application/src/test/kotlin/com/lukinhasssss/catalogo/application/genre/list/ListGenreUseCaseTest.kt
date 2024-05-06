package com.lukinhasssss.catalogo.application.genre.list

import com.lukinhasssss.catalogo.application.UseCaseTest
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.domain.genre.GenreGateway
import com.lukinhasssss.catalogo.domain.pagination.Pagination
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ListGenreUseCaseTest : UseCaseTest() {

    @InjectMockKs
    private lateinit var useCase: ListGenreUseCase

    @MockK
    private lateinit var genreGateway: GenreGateway

    @Test
    fun givenAValidQuery_whenCallsListGenres_shouldReturnIt() {
        // given
        val categories = listOf(Fixture.Genres.business(), Fixture.Genres.tech())

        val expectedItems = categories.map { ListGenreUseCase.Output.from(it) }

        val expectedPage = 0
        val expectedPerPage = 10
        val expectedTerms = "Algo"
        val expectedSort = "name"
        val expectedDirection = "asc"
        val expectedItemsCount = 2

        val aQuery = ListGenreUseCase.Input(
            page = expectedPage,
            perPage = expectedPerPage,
            terms = expectedTerms,
            sort = expectedSort,
            direction = expectedDirection,
            categories = setOf("c1")
        )

        val pagination = Pagination(
            currentPage = expectedPage,
            perPage = expectedPerPage,
            total = categories.size.toLong(),
            items = categories
        )

        every { genreGateway.findAll(any()) } returns pagination

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
