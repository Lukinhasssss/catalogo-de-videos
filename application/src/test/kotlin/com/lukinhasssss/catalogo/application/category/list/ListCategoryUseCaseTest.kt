package com.lukinhasssss.catalogo.application.category.list

import com.lukinhasssss.catalogo.application.UseCaseTest
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.domain.category.CategoryGateway
import com.lukinhasssss.catalogo.domain.category.CategorySearchQuery
import com.lukinhasssss.catalogo.domain.pagination.Pagination
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ListCategoryUseCaseTest : UseCaseTest() {

    @InjectMockKs
    private lateinit var useCase: ListCategoryUseCase

    @MockK
    private lateinit var categoryGateway: CategoryGateway

    @Test
    fun givenAValidQuery_whenCallsListCategories_shouldReturnCategories() {
        // given
        val categories = listOf(Fixture.Categories.lives, Fixture.Categories.aulas)

        val expectedItems = categories.map { ListCategoryOutput.from(it) }
        val expectedPage = 0
        val expectedPerPage = 10
        val expectedTerms = "Algo"
        val expectedSort = "name"
        val expectedDirection = "asc"
        val expectedItemsCount = 2

        val aQuery = CategorySearchQuery(
            page = expectedPage,
            perPage = expectedPerPage,
            terms = expectedTerms,
            sort = expectedSort,
            direction = expectedDirection
        )

        val pagination = Pagination(
            currentPage = expectedPage,
            perPage = expectedPerPage,
            total = categories.size.toLong(),
            items = categories
        )

        every { categoryGateway.findAll(any()) } returns pagination

        // when
        val actualOutput = useCase.execute(aQuery)

        // then
        with(actualOutput) {
            assertEquals(expectedPage, currentPage)
            assertEquals(expectedPerPage, perPage)
            assertEquals(expectedItemsCount, items.size)
            assertTrue(expectedItems.size == items.size && expectedItems.containsAll(items))
        }
    }
}
