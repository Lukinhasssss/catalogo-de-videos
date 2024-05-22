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
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.test.tester.GraphQlTester
import java.time.Instant
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
        val cleanCode = Fixture.Videos.cleanCode()
        val systemDesign = Fixture.Videos.systemDesign()

        val categories = listOf(GetAllCategoriesByIdUseCase.Output(Fixture.Categories.lives))
        val castMembers = listOf(GetAllCastMembersByIdUseCase.Output(Fixture.CastMembers.zoro()))
        val genres = listOf(GetAllGenresByIdUseCase.Output(Fixture.Genres.tech()))

        val expectedVideos = listOf(
            ListVideoUseCase.Output.from(cleanCode),
            ListVideoUseCase.Output.from(systemDesign)
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
                  description
                }
                castMembersId
                castMembers {
                  id
                  name
                  type
                  createdAt
                  updatedAt
                }
                genresId
                genres {
                  id
                  name
                  active
                  categories
                  createdAt
                  updatedAt
                  deletedAt
                }
                createdAt
                updatedAt
              }
            }
        """.trimIndent()

        // when
        val res = graphql.document(query).execute()

        val actualVideos = res.path("videos").entityList(VideoOutput::class.java).get()

        // then
        compareVideoOutput(categories, castMembers, genres, expectedVideos[0], actualVideos[0])
        compareVideoOutput(categories, castMembers, genres, expectedVideos[1], actualVideos[1])

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

        verify { getAllCategoriesByIdUseCase.execute(withArg { assertEquals(it.ids, cleanCode.categories) }) }
        verify { getAllCategoriesByIdUseCase.execute(withArg { assertEquals(it.ids, systemDesign.categories) }) }

        verify { getAllCastMembersByIdUseCase.execute(withArg { assertEquals(it.ids, cleanCode.castMembers) }) }
        verify { getAllCastMembersByIdUseCase.execute(withArg { assertEquals(it.ids, systemDesign.castMembers) }) }

        verify { getAllGenresByIdUseCase.execute(withArg { assertEquals(it.ids, cleanCode.genres) }) }
        verify { getAllGenresByIdUseCase.execute(withArg { assertEquals(it.ids, systemDesign.genres) }) }
    }

    @Test
    fun givenCustomArguments_whenCallsListVideos_shouldReturn() {
        // given
        data class VideoIDOuput(val id: String)

        val cleanCode = Fixture.Videos.cleanCode()
        val systemDesign = Fixture.Videos.systemDesign()

        val expectedVideos = listOf(
            ListVideoUseCase.Output.from(cleanCode),
            ListVideoUseCase.Output.from(systemDesign)
        )

        val expectedVideosId = listOf(
            VideoIDOuput(cleanCode.id),
            VideoIDOuput(systemDesign.id)
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

        val query = """
            query AllVideos(${'$'}search: String, ${'$'}page: Int, ${'$'}perPage: Int, ${'$'}sort: String, ${'$'}direction: String, ${'$'}rating: String, ${'$'}yearLaunched: Int, ${'$'}categories: [String], ${'$'}castMembers: [String], ${'$'}genres: [String]) {
                videos(search: ${'$'}search, page: ${'$'}page, perPage: ${'$'}perPage, sort: ${'$'}sort, direction: ${'$'}direction, rating: ${'$'}rating, yearLaunched: ${'$'}yearLaunched, categories: ${'$'}categories, castMembers: ${'$'}castMembers, genres: ${'$'}genres) {
                    id
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

        val actualVideos = res.path("videos").entityList(VideoIDOuput::class.java).get()

        // then
        assertTrue(actualVideos.size == expectedVideosId.size && actualVideos.containsAll(expectedVideosId))

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

    private data class VideoOutput(
        val id: String,
        val title: String,
        val description: String,
        val yearLaunched: Int,
        val rating: String,
        val duration: Double,
        val opened: Boolean,
        val published: Boolean,
        val banner: String? = null,
        val thumbnail: String? = null,
        val thumbnailHalf: String? = null,
        val trailer: String? = null,
        val video: String? = null,
        val categoriesId: Set<String>,
        val categories: Set<GetAllCategoriesByIdUseCase.Output>,
        val castMembersId: Set<String>,
        val castMembers: Set<GetAllCastMembersByIdUseCase.Output>,
        val genresId: Set<String>,
        val genres: Set<GetAllGenresByIdUseCase.Output>,
        val createdAt: Instant,
        val updatedAt: Instant
    )

    private fun compareVideoOutput(
        expectedCategories: List<GetAllCategoriesByIdUseCase.Output>,
        expectedCastMembers: List<GetAllCastMembersByIdUseCase.Output>,
        expectedGenres: List<GetAllGenresByIdUseCase.Output>,
        expectedVideo: ListVideoUseCase.Output,
        actualVideo: VideoOutput
    ) {
        assertThat(actualVideo)
            .usingRecursiveComparison().ignoringFields("categories", "castMembers", "genres")
            .isEqualTo(expectedVideo)

        assertTrue(
            actualVideo.castMembers.size == expectedCastMembers.size &&
                actualVideo.castMembers.containsAll(expectedCastMembers)
        )

        assertTrue(
            (
                actualVideo.categories.size == expectedCategories.size &&
                    actualVideo.categories.containsAll(expectedCategories)
                )
        )

        assertTrue(
            (
                actualVideo.genres.size == expectedGenres.size &&
                    actualVideo.genres.containsAll(expectedGenres)
                )
        )
    }
}
