package com.lukinhasssss.catalogo.infrastructure.graphql

import com.lukinhasssss.catalogo.GraphQLControllerTest
import com.lukinhasssss.catalogo.application.genre.list.ListGenreUseCase
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.domain.pagination.Pagination
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.test.tester.GraphQlTester
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@GraphQLControllerTest(controllers = [GenreGraphQLController::class])
class GenreGraphQLControllerTest {

    @MockkBean
    private lateinit var listGenreUseCase: ListGenreUseCase

    @Autowired
    private lateinit var graphql: GraphQlTester

    @Test
    fun givenDefaultArguments_whenCallsListGenres_shouldReturn() {
        // given
        val expectedGenres = listOf(
            ListGenreUseCase.Output.from(Fixture.Genres.business()),
            ListGenreUseCase.Output.from(Fixture.Genres.tech())
        )

        val expectedPage = 0
        val expectedPerPage = 10
        val expectedSort = "name"
        val expectedDirection = "asc"
        val expectedSearch = ""
        val expectedCategories = setOf<String>()

        every {
            listGenreUseCase.execute(any())
        } returns Pagination(expectedPage, expectedPerPage, expectedGenres.size.toLong(), expectedGenres)

        val query = """
            {
              genres {
                id
                name
                active
                categories
                createdAt
                updatedAt
                deletedAt
              }
            }
        """.trimIndent()

        // when
        val res = graphql.document(query).execute()

        val actualGenres = res.path("genres").entityList(ListGenreUseCase.Output::class.java).get()

        // then
        assertTrue(actualGenres.size == expectedGenres.size && actualGenres.containsAll(expectedGenres))

        verify {
            listGenreUseCase.execute(
                withArg {
                    assertEquals(expectedPage, it.page)
                    assertEquals(expectedPerPage, it.perPage)
                    assertEquals(expectedSort, it.sort)
                    assertEquals(expectedDirection, it.direction)
                    assertEquals(expectedSearch, it.terms)
                    assertEquals(expectedCategories, it.categories)
                }
            )
        }
    }

    @Test
    fun givenCustomArguments_whenCallsListGenres_shouldReturn() {
        // given
        val expectedGenres = listOf(
            ListGenreUseCase.Output.from(Fixture.Genres.business()),
            ListGenreUseCase.Output.from(Fixture.Genres.tech())
        )

        val expectedPage = 2
        val expectedPerPage = 15
        val expectedSort = "id"
        val expectedDirection = "desc"
        val expectedSearch = "asd"
        val expectedCategories = setOf("c1")

        every {
            listGenreUseCase.execute(any())
        } returns Pagination(expectedPage, expectedPerPage, expectedGenres.size.toLong(), expectedGenres)

        val query = """
            query AllGenres(${'$'}search: String, ${'$'}page: Int, ${'$'}perPage: Int, ${'$'}sort: String, ${'$'}direction: String, ${'$'}categories: [String]) {
                genres(search: ${'$'}search, page: ${'$'}page, perPage: ${'$'}perPage, sort: ${'$'}sort, direction: ${'$'}direction, categories: ${'$'}categories) {
                    id
                    name
                    active
                    categories
                    createdAt
                    updatedAt
                    deletedAt
                }
            }
        """.trimIndent()

        // when
        val res = graphql.document(query)
            .variable("search", expectedSearch)
            .variable("page", expectedPage)
            .variable("perPage", expectedPerPage)
            .variable("sort", expectedSort)
            .variable("direction", expectedDirection)
            .variable("categories", expectedCategories)
            .execute()

        val actualGenres = res.path("genres").entityList(ListGenreUseCase.Output::class.java).get()

        // then
        assertTrue(actualGenres.size == expectedGenres.size && actualGenres.containsAll(expectedGenres))

        verify {
            listGenreUseCase.execute(
                withArg {
                    assertEquals(expectedPage, it.page)
                    assertEquals(expectedPerPage, it.perPage)
                    assertEquals(expectedSort, it.sort)
                    assertEquals(expectedDirection, it.direction)
                    assertEquals(expectedSearch, it.terms)
                    assertEquals(expectedCategories, it.categories)
                }
            )
        }
    }
}
