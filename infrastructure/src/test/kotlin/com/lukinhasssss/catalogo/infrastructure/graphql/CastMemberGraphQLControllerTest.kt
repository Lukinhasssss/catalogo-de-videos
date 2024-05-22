package com.lukinhasssss.catalogo.infrastructure.graphql

import com.lukinhasssss.catalogo.GraphQLControllerTest
import com.lukinhasssss.catalogo.application.castmember.list.ListCastMemberOutput
import com.lukinhasssss.catalogo.application.castmember.list.ListCastMemberUseCase
import com.lukinhasssss.catalogo.application.castmember.save.SaveCastMemberUseCase
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.domain.castmember.CastMemberType
import com.lukinhasssss.catalogo.domain.pagination.Pagination
import com.lukinhasssss.catalogo.domain.utils.IdUtils
import com.lukinhasssss.catalogo.domain.utils.InstantUtils
import com.lukinhasssss.catalogo.infrastructure.castmember.GqlCastMemberPresenter
import com.lukinhasssss.catalogo.infrastructure.castmember.models.GqlCastMember
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.test.tester.GraphQlTester
import java.time.Instant
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@GraphQLControllerTest(controllers = [CastMemberGraphQLController::class])
class CastMemberGraphQLControllerTest {

    @MockkBean
    private lateinit var listCastMemberUseCase: ListCastMemberUseCase

    @MockkBean
    private lateinit var saveCastMemberUseCase: SaveCastMemberUseCase

    @Autowired
    private lateinit var graphql: GraphQlTester

    @Test
    fun givenDefaultArguments_whenCallsListCastMembers_shouldReturn() {
        // given
        val castMembers = listOf(
            ListCastMemberOutput.from(Fixture.CastMembers.luffy()),
            ListCastMemberOutput.from(Fixture.CastMembers.zoro())
        )

        val expectedMembers = castMembers.map { GqlCastMemberPresenter.present(it) }

        val expectedPage = 0
        val expectedPerPage = 10
        val expectedSort = "name"
        val expectedDirection = "asc"
        val expectedSearch = ""

        every {
            listCastMemberUseCase.execute(any())
        } returns Pagination(expectedPage, expectedPerPage, castMembers.size.toLong(), castMembers)

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

        val actualMembers = res.path("castMembers").entityList(GqlCastMember::class.java).get()

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
        val castMembers = listOf(
            ListCastMemberOutput.from(Fixture.CastMembers.luffy()),
            ListCastMemberOutput.from(Fixture.CastMembers.zoro())
        )

        val expectedMembers = castMembers.map { GqlCastMemberPresenter.present(it) }

        val expectedPage = 2
        val expectedPerPage = 15
        val expectedSort = "id"
        val expectedDirection = "desc"
        val expectedSearch = "asd"

        every {
            listCastMemberUseCase.execute(any())
        } returns Pagination(expectedPage, expectedPerPage, castMembers.size.toLong(), castMembers)

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

        val actualMembers = res.path("castMembers").entityList(GqlCastMember::class.java).get()

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
    fun givenCastMemberInput_whenCallsSaveCastMemberMutation_shouldPersistAndReturn() {
        // given
        val expectedId = IdUtils.uuid()
        val expectedName = "Luffy"
        val expectedType = CastMemberType.ACTOR
        val expectedCreatedAt = InstantUtils.now()
        val expectedUpdatedAt = InstantUtils.now()

        val input = mapOf(
            "id" to expectedId,
            "name" to expectedName,
            "type" to expectedType,
            "createdAt" to expectedCreatedAt,
            "updatedAt" to expectedUpdatedAt
        )

        val query = """
            mutation SaveCastMember(${'$'}input: CastMemberInput!) {
                castMember: saveCastMember(input: ${'$'}input) {
                    id
                    name
                    type
                    createdAt
                    updatedAt
                }
            }
        """.trimIndent()

        every { saveCastMemberUseCase.execute(any()) } answers { firstArg() }

        // when
        graphql.document(query)
            .variable("input", input)
            .execute()
            .path("castMember.id").entity(String::class.java).isEqualTo(expectedId)
            .path("castMember.name").entity(String::class.java).isEqualTo(expectedName)
            .path("castMember.type").entity(CastMemberType::class.java).isEqualTo(expectedType)
            .path("castMember.createdAt").entity(Instant::class.java).isEqualTo(expectedCreatedAt)
            .path("castMember.updatedAt").entity(Instant::class.java).isEqualTo(expectedUpdatedAt)

        // then
        verify {
            saveCastMemberUseCase.execute(
                withArg {
                    assertEquals(expectedId, it.id)
                    assertEquals(expectedName, it.name)
                    assertEquals(expectedType, it.type)
                    assertEquals(expectedCreatedAt, it.createdAt)
                    assertEquals(expectedUpdatedAt, it.updatedAt)
                }
            )
        }
    }
}
