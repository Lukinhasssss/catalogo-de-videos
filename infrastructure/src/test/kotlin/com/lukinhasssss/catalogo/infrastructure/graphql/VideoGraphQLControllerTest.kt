package com.lukinhasssss.catalogo.infrastructure.graphql

import com.lukinhasssss.catalogo.GraphQLControllerTest
import com.lukinhasssss.catalogo.application.castmember.get.GetAllCastMembersByIdUseCase
import com.lukinhasssss.catalogo.application.category.get.GetAllCategoriesByIdUseCase
import com.lukinhasssss.catalogo.application.genre.get.GetAllGenresByIdUseCase
import com.lukinhasssss.catalogo.application.video.list.ListVideoUseCase
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

@GraphQLControllerTest(controllers = [VideoGraphQLController::class])
class VideoGraphQLControllerTest {

    @MockkBean
    private lateinit var listVideoUseCase: ListVideoUseCase

    @MockkBean
    private lateinit var getAllCategoriesByIdUseCase: GetAllCategoriesByIdUseCase

    @MockkBean
    private lateinit var getAllCastMembersByIdUseCase: GetAllCastMembersByIdUseCase

    @MockkBean
    private lateinit var getAllGenresByIdUseCase: GetAllGenresByIdUseCase

    @Autowired
    private lateinit var graphql: GraphQlTester

    @Test
    fun givenDefaultArguments_whenCallsListVideos_shouldReturn() {
        // given
        val categories = listOf(GetAllCategoriesByIdUseCase.Output(Fixture.Categories.lives))
        val castMembers = listOf(GetAllCastMembersByIdUseCase.Output(Fixture.CastMembers.zoro()))
        val genres = listOf(GetAllGenresByIdUseCase.Output(Fixture.Genres.tech()))

        val expectedVideos = listOf(
            ListVideoUseCase.Output.from(Fixture.Videos.cleanCode()),
            ListVideoUseCase.Output.from(Fixture.Videos.systemDesign())
        )

        val expectedPage = 0
        val expectedPerPage = 10
        val expectedSort = "title"
        val expectedDirection = "asc"
        val expectedSearch = ""
        val expectedRating: String? = null
        val expectedYearLaunched: Int? = null
        val expectedCategories = setOf<String>()
        val expectedCastMembers = setOf<String>()
        val expectedGenres = setOf<String>()

        every {
            listVideoUseCase.execute(any())
        } returns Pagination(expectedPage, expectedPerPage, expectedVideos.size.toLong(), expectedVideos)

        every { getAllCategoriesByIdUseCase.execute(any()) } returns categories
        every { getAllCastMembersByIdUseCase.execute(any()) } returns castMembers
        every { getAllGenresByIdUseCase.execute(any()) } returns genres

        val query = """
            {
              videos {
                id
                title
                description
                yearLaunched
                rating
                duration
                opened
                published
                banner
                thumbnail
                thumbnailHalf
                trailer
                video
                categoriesId
                categories {
                  id
                  name
                }
                castMembersId
                castMembers {
                  id
                  name
                }
                genresId
                genres {
                  id
                  name
                }
                createdAt
                updatedAt
              }
            }
        """.trimIndent()

        // when
        val res = graphql.document(query).execute()

        val actualVideos = res.path("videos").entityList(ListVideoUseCase.Output::class.java).get()

        // then
        assertTrue(actualVideos.size == expectedVideos.size && actualVideos.containsAll(expectedVideos))

        verify {
            listVideoUseCase.execute(
                withArg {
                    assertEquals(expectedPage, it.page)
                    assertEquals(expectedPerPage, it.perPage)
                    assertEquals(expectedSort, it.sort)
                    assertEquals(expectedDirection, it.direction)
                    assertEquals(expectedSearch, it.terms)
                    assertEquals(expectedRating, it.rating)
                    assertEquals(expectedYearLaunched, it.launchedAt)
                    assertEquals(expectedCategories, it.categories)
                    assertEquals(expectedCastMembers, it.castMembers)
                    assertEquals(expectedGenres, it.genres)
                }
            )
        }
    }

    @Test
    fun givenCustomArguments_whenCallsListVideos_shouldReturn() {
        // given
        val categories = listOf(GetAllCategoriesByIdUseCase.Output(Fixture.Categories.lives))
        val castMembers = listOf(GetAllCastMembersByIdUseCase.Output(Fixture.CastMembers.zoro()))
        val genres = listOf(GetAllGenresByIdUseCase.Output(Fixture.Genres.tech()))

        val expectedVideos = listOf(
            ListVideoUseCase.Output.from(Fixture.Videos.cleanCode()),
            ListVideoUseCase.Output.from(Fixture.Videos.systemDesign())
        )

        val expectedPage = 2
        val expectedPerPage = 15
        val expectedSort = "id"
        val expectedDirection = "desc"
        val expectedSearch = "asd"
        val expectedRating = "L"
        val expectedYearLaunched = 2024
        val expectedCategories = setOf("c1")
        val expectedCastMembers = setOf("cm1")
        val expectedGenres = setOf("g1")

        every {
            listVideoUseCase.execute(any())
        } returns Pagination(expectedPage, expectedPerPage, expectedVideos.size.toLong(), expectedVideos)

        every { getAllCategoriesByIdUseCase.execute(any()) } returns categories
        every { getAllCastMembersByIdUseCase.execute(any()) } returns castMembers
        every { getAllGenresByIdUseCase.execute(any()) } returns genres

        val query = """
            query AllVideos(${'$'}search: String, ${'$'}page: Int, ${'$'}perPage: Int, ${'$'}sort: String, ${'$'}direction: String, ${'$'}rating: String, ${'$'}yearLaunched: Int, ${'$'}categories: [String], ${'$'}castMembers: [String], ${'$'}genres: [String]) {
                videos(search: ${'$'}search, page: ${'$'}page, perPage: ${'$'}perPage, sort: ${'$'}sort, direction: ${'$'}direction, rating: ${'$'}rating, yearLaunched: ${'$'}yearLaunched, categories: ${'$'}categories, castMembers: ${'$'}castMembers, genres: ${'$'}genres) {
                    id
                    title
                    description
                    yearLaunched
                    rating
                    duration
                    opened
                    published
                    banner
                    thumbnail
                    thumbnailHalf
                    trailer
                    video
                    categoriesId
                    categories {
                      id
                      name
                    }
                    castMembersId
                    castMembers {
                      id
                      name
                    }
                    genresId
                    genres {
                      id
                      name
                    }
                    createdAt
                    updatedAt
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
            .variable("rating", expectedRating)
            .variable("yearLaunched", expectedYearLaunched)
            .variable("categories", expectedCategories)
            .variable("castMembers", expectedCastMembers)
            .variable("genres", expectedGenres)
            .execute()

        val actualVideos = res.path("videos").entityList(ListVideoUseCase.Output::class.java).get()

        // then
        assertTrue(actualVideos.size == expectedVideos.size && actualVideos.containsAll(expectedVideos))

        verify {
            listVideoUseCase.execute(
                withArg {
                    assertEquals(expectedPage, it.page)
                    assertEquals(expectedPerPage, it.perPage)
                    assertEquals(expectedSort, it.sort)
                    assertEquals(expectedDirection, it.direction)
                    assertEquals(expectedSearch, it.terms)
                    assertEquals(expectedRating, it.rating)
                    assertEquals(expectedYearLaunched, it.launchedAt)
                    assertEquals(expectedCategories, it.categories)
                    assertEquals(expectedCastMembers, it.castMembers)
                    assertEquals(expectedGenres, it.genres)
                }
            )
        }
    }
}
