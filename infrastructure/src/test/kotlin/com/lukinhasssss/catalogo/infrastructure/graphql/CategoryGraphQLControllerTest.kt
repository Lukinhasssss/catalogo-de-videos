package com.lukinhasssss.catalogo.infrastructure.graphql

import com.lukinhasssss.catalogo.application.category.list.ListCategoryOutput
import com.lukinhasssss.catalogo.application.category.list.ListCategoryUseCase
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.domain.pagination.Pagination
import com.lukinhasssss.catalogo.infrastructure.GraphQLControllerTest
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.test.tester.GraphQlTester
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@GraphQLControllerTest
class CategoryGraphQLControllerTest {

    @MockkBean
    private lateinit var listCategoryUseCase: ListCategoryUseCase

    @Autowired
    private lateinit var graphql: GraphQlTester

    @Test
    fun givenDefaultArguments_whenCallsListCategories_shouldReturn() {
        // given
        val expectedCategories = listOf(
            ListCategoryOutput.from(Fixture.Categories.lives),
            ListCategoryOutput.from(Fixture.Categories.aulas)
        )

        val expectedPage = 0
        val expectedPerPage = 10
        val expectedSort = "name"
        val expectedDirection = "asc"
        val expectedSearch = ""

        every {
            listCategoryUseCase.execute(any())
        } returns Pagination(expectedPage, expectedPerPage, expectedCategories.size.toLong(), expectedCategories)

        val query = """
            {
              categories {
                id
                name
              }
            }
        """.trimIndent()

        // when
        val res = graphql.document(query).execute()

        val actualCategories = res.path("categories").entityList(ListCategoryOutput::class.java).get()

        // then
        assertTrue(actualCategories.size == expectedCategories.size && actualCategories.containsAll(expectedCategories))

        verify {
            listCategoryUseCase.execute(
                withArg {
                    assertEquals(expectedPage, it.page)
                    assertEquals(expectedPerPage, it.perPage)
                    assertEquals(expectedSort, it.sort)
                    assertEquals(expectedDirection, it.direction)
                    assertEquals(expectedSearch, it.terms)
                }
            )
        }
    }

    @Test
    fun givenCustomArguments_whenCallsListCategories_shouldReturn() {
        // given
        val expectedCategories = listOf(
            ListCategoryOutput.from(Fixture.Categories.lives),
            ListCategoryOutput.from(Fixture.Categories.aulas)
        )

        val expectedPage = 2
        val expectedPerPage = 15
        val expectedSort = "id"
        val expectedDirection = "desc"
        val expectedSearch = "asd"

        every {
            listCategoryUseCase.execute(any())
        } returns Pagination(expectedPage, expectedPerPage, expectedCategories.size.toLong(), expectedCategories)

        val query = """
            {
              categories(search: "%s", page: %s, perPage: %s, sort: "%s", direction: "%s") {
                id
                name
              }
            }
        """.trimIndent().format(expectedSearch, expectedPage, expectedPerPage, expectedSort, expectedDirection)

        // when
        val res = graphql.document(query).execute()

        val actualCategories = res.path("categories").entityList(ListCategoryOutput::class.java).get()

        // then
        assertTrue(actualCategories.size == expectedCategories.size && actualCategories.containsAll(expectedCategories))

        verify {
            listCategoryUseCase.execute(
                withArg {
                    assertEquals(expectedPage, it.page)
                    assertEquals(expectedPerPage, it.perPage)
                    assertEquals(expectedSort, it.sort)
                    assertEquals(expectedDirection, it.direction)
                    assertEquals(expectedSearch, it.terms)
                }
            )
        }
    }
}
