package com.lukinhasssss.catalogo.infrastructure.graphql

import com.lukinhasssss.catalogo.IntegrationTest
import com.lukinhasssss.catalogo.WebGraphQlSecurityInterceptor
import com.lukinhasssss.catalogo.application.video.list.ListVideoUseCase
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
class VideoGraphQLIT {

    @MockkBean
    private lateinit var listVideoUseCase: ListVideoUseCase

    @Autowired
    private lateinit var graphqlHandler: WebGraphQlHandler

    @Autowired
    private lateinit var interceptor: WebGraphQlSecurityInterceptor

    @Test
    fun givenAnonymousUser_whenQueries_shouldReturnUnauthorized() {
        interceptor.setAuthorities()

        val document = "query { videos { id } }"

        val graphQlTester = WebGraphQlTester.create(graphqlHandler)

        graphQlTester.document(document).execute()
            .errors()
            .expect { it.path == "videos" && it.message == "Unauthorized" }
            .verify()
    }

    @Test
    fun givenUserWithAdminRole_whenQueries_shouldReturnVideos() {
        interceptor.setAuthorities(Roles.ADMIN)

        val videos = listOf(
            ListVideoUseCase.Output.from(Fixture.Videos.golang()),
            ListVideoUseCase.Output.from(Fixture.Videos.cleanCode())
        )

        val expectedIds = videos.map { it.id }

        every {
            listVideoUseCase.execute(any())
        } returns Pagination(0, 10, videos.size.toLong(), videos)

        val document = "query { videos { id } }"

        val graphQlTester = WebGraphQlTester.create(graphqlHandler)

        graphQlTester.document(document).execute().errors().verify()
            .path("videos[*].id").entityList(String::class.java).isEqualTo<GraphQlTester.EntityList<String>>(expectedIds)
    }

    @Test
    fun givenUserWithSubscriberRole_whenQueries_shouldReturnVideos() {
        interceptor.setAuthorities(Roles.SUBSCRIBER)

        val videos = listOf(
            ListVideoUseCase.Output.from(Fixture.Videos.golang()),
            ListVideoUseCase.Output.from(Fixture.Videos.cleanCode())
        )

        val expectedIds = videos.map { it.id }

        every {
            listVideoUseCase.execute(any())
        } returns Pagination(0, 10, videos.size.toLong(), videos)

        val document = "query { videos { id } }"

        val graphQlTester = WebGraphQlTester.create(graphqlHandler)

        graphQlTester.document(document).execute().errors().verify()
            .path("videos[*].id").entityList(String::class.java).isEqualTo<GraphQlTester.EntityList<String>>(expectedIds)
    }

    @Test
    fun givenUserWithVideosRole_whenQueries_shouldReturnVideos() {
        interceptor.setAuthorities(Roles.VIDEOS)

        val videos = listOf(
            ListVideoUseCase.Output.from(Fixture.Videos.golang()),
            ListVideoUseCase.Output.from(Fixture.Videos.cleanCode())
        )

        val expectedIds = videos.map { it.id }

        every {
            listVideoUseCase.execute(any())
        } returns Pagination(0, 10, videos.size.toLong(), videos)

        val document = "query { videos { id } }"

        val graphQlTester = WebGraphQlTester.create(graphqlHandler)

        graphQlTester.document(document).execute().errors().verify()
            .path("videos[*].id").entityList(String::class.java).isEqualTo<GraphQlTester.EntityList<String>>(expectedIds)
    }
}
