package com.lukinhasssss.catalogo.infrastructure.graphql

import com.lukinhasssss.catalogo.GraphQLControllerTest
import com.lukinhasssss.catalogo.application.genre.list.ListGenreUseCase
import com.lukinhasssss.catalogo.application.genre.save.SaveGenreUseCase
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.domain.pagination.Pagination
import com.lukinhasssss.catalogo.domain.utils.IdUtils
import com.lukinhasssss.catalogo.domain.utils.InstantUtils
import com.lukinhasssss.catalogo.infrastructure.genre.GqlGenrePresenter
import com.lukinhasssss.catalogo.infrastructure.genre.models.GqlGenre
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.test.tester.GraphQlTester
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@GraphQLControllerTest(controllers = [GenreGraphQLController::class])
class GenreGraphQLControllerTest {

    @MockkBean
    private lateinit var listGenreUseCase: ListGenreUseCase

    @MockkBean
    private lateinit var saveGenreUseCase: SaveGenreUseCase

    @Autowired
    private lateinit var graphql: GraphQlTester

    @Test
    fun givenDefaultArguments_whenCallsListGenres_shouldReturn() {
        // given
        val genres = listOf(
            ListGenreUseCase.Output.from(Fixture.Genres.business()),
            ListGenreUseCase.Output.from(Fixture.Genres.tech())
        )

        val expectedGenres = genres.map { GqlGenrePresenter.present(it) }

        val expectedPage = 0
        val expectedPerPage = 10
        val expectedSort = "name"
        val expectedDirection = "asc"
        val expectedSearch = ""
        val expectedCategories = setOf<String>()

        every {
            listGenreUseCase.execute(any())
        } returns Pagination(expectedPage, expectedPerPage, genres.size.toLong(), genres)

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

        val actualGenres = res.path("genres").entityList(GqlGenre::class.java).get()

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
        val genres = listOf(
            ListGenreUseCase.Output.from(Fixture.Genres.business()),
            ListGenreUseCase.Output.from(Fixture.Genres.tech())
        )

        val expectedGenres = genres.map { GqlGenrePresenter.present(it) }

        val expectedPage = 2
        val expectedPerPage = 15
        val expectedSort = "id"
        val expectedDirection = "desc"
        val expectedSearch = "asd"
        val expectedCategories = setOf("c1")

        every {
            listGenreUseCase.execute(any())
        } returns Pagination(expectedPage, expectedPerPage, genres.size.toLong(), genres)

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

        val actualGenres = res.path("genres").entityList(GqlGenre::class.java).get()

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
    fun givenInactiveGenreInput_whenCallsSaveGenreMutation_shouldPersistAndReturn() {
        // given
        val expectedId = IdUtils.uuid()
        val expectedName = "Business"
        val expectedIsActive = false
        val expectedCategories = setOf("c1", "c2")
        val expectedCreatedAt = InstantUtils.now()
        val expectedUpdatedAt = InstantUtils.now()
        val expectedDeletedAt = InstantUtils.now()

        val input = mapOf(
            "id" to expectedId,
            "name" to expectedName,
            "active" to expectedIsActive,
            "categories" to expectedCategories,
            "createdAt" to expectedCreatedAt.toString(),
            "updatedAt" to expectedUpdatedAt.toString(),
            "deletedAt" to expectedDeletedAt.toString()
        )

        val query = """
            mutation SaveGenre(${'$'}input: GenreInput!) {
                genre: saveGenre(input: ${'$'}input) {
                    id
                }
            }
        """.trimIndent()

        every { saveGenreUseCase.execute(any()) } answers { SaveGenreUseCase.Output(expectedId) }

        // when
        graphql.document(query)
            .variable("input", input)
            .execute()
            .path("genre.id").entity(String::class.java).isEqualTo(expectedId)

        // then
        verify {
            saveGenreUseCase.execute(
                withArg {
                    assertEquals(expectedId, it.id)
                    assertEquals(expectedName, it.name)
                    assertEquals(expectedIsActive, it.isActive)
                    assertEquals(expectedCategories, it.categories)
                    assertEquals(expectedCreatedAt, it.createdAt)
                    assertEquals(expectedUpdatedAt, it.updatedAt)
                    assertEquals(expectedDeletedAt, it.deletedAt)
                }
            )
        }
    }

    @Test
    fun givenActiveGenreInputWithoutDeletedAt_whenCallsSaveGenreMutation_shouldPersistAndReturn() {
        // given
        val expectedId = IdUtils.uuid()
        val expectedName = "Business"
        val expectedIsActive = true
        val expectedCategories = setOf("c1", "c2")
        val expectedCreatedAt = InstantUtils.now()
        val expectedUpdatedAt = InstantUtils.now()
        val expectedDeletedAt: Instant? = null

        val input = mapOf(
            "id" to expectedId,
            "name" to expectedName,
            "active" to expectedIsActive,
            "categories" to expectedCategories,
            "createdAt" to expectedCreatedAt.toString(),
            "updatedAt" to expectedUpdatedAt.toString()
        )

        val query = """
            mutation SaveGenre(${'$'}input: GenreInput!) {
                genre: saveGenre(input: ${'$'}input) {
                    id
                }
            }
        """.trimIndent()

        every { saveGenreUseCase.execute(any()) } answers { SaveGenreUseCase.Output(expectedId) }

        // when
        graphql.document(query)
            .variable("input", input)
            .execute()
            .path("genre.id").entity(String::class.java).isEqualTo(expectedId)

        // then
        verify {
            saveGenreUseCase.execute(
                withArg {
                    assertEquals(expectedId, it.id)
                    assertEquals(expectedName, it.name)
                    assertEquals(expectedIsActive, it.isActive)
                    assertEquals(expectedCategories, it.categories)
                    assertEquals(expectedCreatedAt, it.createdAt)
                    assertEquals(expectedUpdatedAt, it.updatedAt)
                    assertEquals(expectedDeletedAt, it.deletedAt)
                }
            )
        }
    }
}
