package com.lukinhasssss.catalogo.infrastructure.graphql

import com.lukinhasssss.catalogo.GraphQLControllerTest
import com.lukinhasssss.catalogo.application.category.list.ListCategoryOutput
import com.lukinhasssss.catalogo.application.category.list.ListCategoryUseCase
import com.lukinhasssss.catalogo.application.category.save.SaveCategoryUseCase
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.domain.pagination.Pagination
import com.lukinhasssss.catalogo.domain.utils.IdUtils
import com.lukinhasssss.catalogo.domain.utils.InstantUtils
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.test.tester.GraphQlTester
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@GraphQLControllerTest(controllers = [CategoryGraphQLController::class])
class CategoryGraphQLControllerTest {

    @MockkBean
    private lateinit var listCategoryUseCase: ListCategoryUseCase

    @MockkBean
    private lateinit var saveCategoryUseCase: SaveCategoryUseCase

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
            query AllCategories(${'$'}search: String, ${'$'}page: Int, ${'$'}perPage: Int, ${'$'}sort: String, ${'$'}direction: String) {
                categories(search: ${'$'}search, page: ${'$'}page, perPage: ${'$'}perPage, sort: ${'$'}sort, direction: ${'$'}direction) {
                    id
                    name
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
            .execute()

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
    fun givenCategoryInput_whenCallsSaveCategoryMutation_shouldPersistAndReturn() {
        // given
        val expectedId = IdUtils.uuid()
        val expectedName = "Animes"
        val expectedDescription = "A melhor categoria"
        val expectedActive = false
        val expectedCreatedAt = InstantUtils.now()
        val expectedUpdatedAt = InstantUtils.now()
        val expectedDeletedAt = InstantUtils.now()

        val input = mapOf(
            "id" to expectedId,
            "name" to expectedName,
            "description" to expectedDescription,
            "active" to expectedActive,
            "createdAt" to expectedCreatedAt,
            "updatedAt" to expectedUpdatedAt,
            "deletedAt" to expectedDeletedAt
        )

        val query = """
            mutation SaveCategory(${'$'}input: CategoryInput!) {
                category: saveCategory(input: ${'$'}input) {
                    id
                    name
                    description
                }
            }
        """.trimIndent()

        every { saveCategoryUseCase.execute(any()) } answers { firstArg() }

        // when
        graphql.document(query)
            .variable("input", input)
            .execute()
            .path("category.id").entity(String::class.java).isEqualTo(expectedId)
            .path("category.name").entity(String::class.java).isEqualTo(expectedName)
            .path("category.description").entity(String::class.java).isEqualTo(expectedDescription)

        // then
        verify {
            saveCategoryUseCase.execute(
                withArg {
                    assertEquals(expectedId, it.id)
                    assertEquals(expectedName, it.name)
                    assertEquals(expectedDescription, it.description)
                    assertEquals(expectedActive, it.isActive)
                    assertEquals(expectedCreatedAt, it.createdAt)
                    assertEquals(expectedUpdatedAt, it.updatedAt)
                    assertEquals(expectedDeletedAt, it.deletedAt)
                }
            )
        }
    }
}
