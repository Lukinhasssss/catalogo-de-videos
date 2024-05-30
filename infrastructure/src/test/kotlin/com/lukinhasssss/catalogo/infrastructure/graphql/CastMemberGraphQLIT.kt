package com.lukinhasssss.catalogo.infrastructure.graphql

import com.lukinhasssss.catalogo.IntegrationTest
import com.lukinhasssss.catalogo.WebGraphQlSecurityInterceptor
import com.lukinhasssss.catalogo.application.castmember.list.ListCastMemberOutput
import com.lukinhasssss.catalogo.application.castmember.list.ListCastMemberUseCase
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.domain.pagination.Pagination
import com.lukinhasssss.catalogo.infrastructure.configuration.security.Roles
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.graphql.server.WebGraphQlHandler
import org.springframework.graphql.test.tester.GraphQlTester
import org.springframework.graphql.test.tester.WebGraphQlTester

@IntegrationTest
class CastMemberGraphQLIT {

    @MockkBean
    private lateinit var listCastMemberUseCase: ListCastMemberUseCase

    @Autowired
    private lateinit var graphqlHandler: WebGraphQlHandler

    @Autowired
    private lateinit var interceptor: WebGraphQlSecurityInterceptor

    @Test
    fun givenAnonymousUser_whenQueries_shouldReturnUnauthorized() {
        interceptor.setAuthorities()

        val document = "query { castMembers { id } }"

        val graphQlTester = WebGraphQlTester.create(graphqlHandler)

        graphQlTester.document(document).execute()
            .errors()
            .expect { it.path == "castMembers" && it.message == "Unauthorized" }
            .verify()
    }

    @Test
    fun givenUserWithAdminRole_whenQueries_shouldReturnCastMembers() {
        interceptor.setAuthorities(Roles.ADMIN)

        val castMembers = listOf(
            ListCastMemberOutput.from(Fixture.CastMembers.luffy()),
            ListCastMemberOutput.from(Fixture.CastMembers.zoro())
        )

        val expectedIds = castMembers.map { it.id }

        every {
            listCastMemberUseCase.execute(any())
        } returns Pagination(0, 10, castMembers.size.toLong(), castMembers)

        val document = "query { castMembers { id } }"

        val graphQlTester = WebGraphQlTester.create(graphqlHandler)

        graphQlTester.document(document).execute().errors().verify()
            .path("castMembers[*].id").entityList(String::class.java).isEqualTo<GraphQlTester.EntityList<String>>(expectedIds)
    }

    @Test
    fun givenUserWithSubscriberRole_whenQueries_shouldReturnCastMembers() {
        interceptor.setAuthorities(Roles.SUBSCRIBER)

        val castMembers = listOf(
            ListCastMemberOutput.from(Fixture.CastMembers.luffy()),
            ListCastMemberOutput.from(Fixture.CastMembers.zoro())
        )

        val expectedIds = castMembers.map { it.id }

        every {
            listCastMemberUseCase.execute(any())
        } returns Pagination(0, 10, castMembers.size.toLong(), castMembers)

        val document = "query { castMembers { id } }"

        val graphQlTester = WebGraphQlTester.create(graphqlHandler)

        graphQlTester.document(document).execute().errors().verify()
            .path("castMembers[*].id").entityList(String::class.java).isEqualTo<GraphQlTester.EntityList<String>>(expectedIds)
    }

    @Test
    fun givenUserWithCastMembersRole_whenQueries_shouldReturnCastMembers() {
        interceptor.setAuthorities(Roles.CAST_MEMBERS)

        val castMembers = listOf(
            ListCastMemberOutput.from(Fixture.CastMembers.luffy()),
            ListCastMemberOutput.from(Fixture.CastMembers.zoro())
        )

        val expectedIds = castMembers.map { it.id }

        every {
            listCastMemberUseCase.execute(any())
        } returns Pagination(0, 10, castMembers.size.toLong(), castMembers)

        val document = "query { castMembers { id } }"

        val graphQlTester = WebGraphQlTester.create(graphqlHandler)

        graphQlTester.document(document).execute().errors().verify()
            .path("castMembers[*].id").entityList(String::class.java).isEqualTo<GraphQlTester.EntityList<String>>(expectedIds)
    }
}
