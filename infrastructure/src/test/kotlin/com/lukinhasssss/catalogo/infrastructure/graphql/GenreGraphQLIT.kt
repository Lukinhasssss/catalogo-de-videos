package com.lukinhasssss.catalogo.infrastructure.graphql

import com.lukinhasssss.catalogo.IntegrationTest
import com.lukinhasssss.catalogo.WebGraphQlSecurityInterceptor
import com.lukinhasssss.catalogo.application.genre.list.ListGenreUseCase
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
class GenreGraphQLIT {

    @MockkBean
    private lateinit var listGenreUseCase: ListGenreUseCase

    @Autowired
    private lateinit var graphqlHandler: WebGraphQlHandler

    @Autowired
    private lateinit var interceptor: WebGraphQlSecurityInterceptor

    @Test
    fun givenAnonymousUser_whenQueries_shouldReturnUnauthorized() {
        interceptor.setAuthorities()

        val document = "query { genres { id } }"

        val graphQlTester = WebGraphQlTester.create(graphqlHandler)

        graphQlTester.document(document).execute()
            .errors()
            .expect { it.path == "genres" && it.message == "Unauthorized" }
            .verify()
    }

    @Test
    fun givenUserWithAdminRole_whenQueries_shouldReturnGenres() {
        interceptor.setAuthorities(Roles.ADMIN)

        val genres = listOf(
            ListGenreUseCase.Output.from(Fixture.Genres.tech()),
            ListGenreUseCase.Output.from(Fixture.Genres.business())
        )

        val expectedIds = genres.map { it.id }

        every {
            listGenreUseCase.execute(any())
        } returns Pagination(0, 10, genres.size.toLong(), genres)

        val document = "query { genres { id } }"

        val graphQlTester = WebGraphQlTester.create(graphqlHandler)

        graphQlTester.document(document).execute().errors().verify()
            .path("genres[*].id").entityList(String::class.java).isEqualTo<GraphQlTester.EntityList<String>>(expectedIds)
    }

    @Test
    fun givenUserWithSubscriberRole_whenQueries_shouldReturnGenres() {
        interceptor.setAuthorities(Roles.SUBSCRIBER)

        val genres = listOf(
            ListGenreUseCase.Output.from(Fixture.Genres.tech()),
            ListGenreUseCase.Output.from(Fixture.Genres.business())
        )

        val expectedIds = genres.map { it.id }

        every {
            listGenreUseCase.execute(any())
        } returns Pagination(0, 10, genres.size.toLong(), genres)

        val document = "query { genres { id } }"

        val graphQlTester = WebGraphQlTester.create(graphqlHandler)

        graphQlTester.document(document).execute().errors().verify()
            .path("genres[*].id").entityList(String::class.java).isEqualTo<GraphQlTester.EntityList<String>>(expectedIds)
    }

    @Test
    fun givenUserWithGenresRole_whenQueries_shouldReturnGenres() {
        interceptor.setAuthorities(Roles.GENRES)

        val genres = listOf(
            ListGenreUseCase.Output.from(Fixture.Genres.tech()),
            ListGenreUseCase.Output.from(Fixture.Genres.business())
        )

        val expectedIds = genres.map { it.id }

        every {
            listGenreUseCase.execute(any())
        } returns Pagination(0, 10, genres.size.toLong(), genres)

        val document = "query { genres { id } }"

        val graphQlTester = WebGraphQlTester.create(graphqlHandler)

        graphQlTester.document(document).execute().errors().verify()
            .path("genres[*].id").entityList(String::class.java).isEqualTo<GraphQlTester.EntityList<String>>(expectedIds)
    }
}
