package com.lukinhasssss.catalogo.infrastructure.graphql

import com.lukinhasssss.catalogo.GraphQLControllerTest
import com.lukinhasssss.catalogo.application.castmember.list.ListCastMemberOutput
import com.lukinhasssss.catalogo.application.castmember.list.ListCastMemberUseCase
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

@GraphQLControllerTest(controllers = [CastMemberGraphQLController::class])
class CastMemberGraphQLControllerTest {

    @MockkBean
    private lateinit var listCastMemberUseCase: ListCastMemberUseCase

    @Autowired
    private lateinit var graphql: GraphQlTester

    @Test
    fun givenDefaultArguments_whenCallsListCastMembers_shouldReturn() {
        // given
        val expectedMembers = listOf(
            ListCastMemberOutput.from(Fixture.CastMembers.luffy()),
            ListCastMemberOutput.from(Fixture.CastMembers.zoro())
        )

        val expectedPage = 0
        val expectedPerPage = 10
        val expectedSort = "name"
        val expectedDirection = "asc"
        val expectedSearch = ""

        every {
            listCastMemberUseCase.execute(any())
        } returns Pagination(expectedPage, expectedPerPage, expectedMembers.size.toLong(), expectedMembers)

        val query = """
            {
              castMembers {
                id
                name
                type
                createdAt
                updatedAt
              }
            }
        """.trimIndent()

        // when
        val res = graphql.document(query).execute()

        val actualMembers = res.path("castMembers").entityList(ListCastMemberOutput::class.java).get()

        // then
        assertTrue(actualMembers.size == expectedMembers.size && actualMembers.containsAll(expectedMembers))

        verify {
            listCastMemberUseCase.execute(
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
    fun givenCustomArguments_whenCallsListCastMembers_shouldReturn() {
        // given
        val expectedMembers = listOf(
            ListCastMemberOutput.from(Fixture.CastMembers.luffy()),
            ListCastMemberOutput.from(Fixture.CastMembers.zoro())
        )

        val expectedPage = 2
        val expectedPerPage = 15
        val expectedSort = "id"
        val expectedDirection = "desc"
        val expectedSearch = "asd"

        every {
            listCastMemberUseCase.execute(any())
        } returns Pagination(expectedPage, expectedPerPage, expectedMembers.size.toLong(), expectedMembers)

        val query = """
            query AllCastMembers(${'$'}search: String, ${'$'}page: Int, ${'$'}perPage: Int, ${'$'}sort: String, ${'$'}direction: String) {
                castMembers(search: ${'$'}search, page: ${'$'}page, perPage: ${'$'}perPage, sort: ${'$'}sort, direction: ${'$'}direction) {
                    id
                    name
                    type
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
            .execute()

        val actualMembers = res.path("castMembers").entityList(ListCastMemberOutput::class.java).get()

        // then
        assertTrue(actualMembers.size == expectedMembers.size && actualMembers.containsAll(expectedMembers))

        verify {
            listCastMemberUseCase.execute(
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
