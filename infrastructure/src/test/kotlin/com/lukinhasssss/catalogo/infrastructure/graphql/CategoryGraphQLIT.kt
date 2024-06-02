package com.lukinhasssss.catalogo.infrastructure.graphql

import com.lukinhasssss.catalogo.IntegrationTest
import com.lukinhasssss.catalogo.WebGraphQlSecurityInterceptor
import com.lukinhasssss.catalogo.application.category.list.ListCategoryOutput
import com.lukinhasssss.catalogo.application.category.list.ListCategoryUseCase
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
class CategoryGraphQLIT {

    @MockkBean
    private lateinit var listCategoryUseCase: ListCategoryUseCase

    @Autowired
    private lateinit var graphqlHandler: WebGraphQlHandler

    @Autowired
    private lateinit var interceptor: WebGraphQlSecurityInterceptor

    @Test
    fun givenAnonymousUser_whenQueries_shouldReturnUnauthorized() {
        interceptor.setAuthorities()

        val document = "query { categories { id } }"

        val graphQlTester = WebGraphQlTester.create(graphqlHandler)

        graphQlTester.document(document).execute()
            .errors()
            .expect { it.path == "categories" && it.message == "Unauthorized" }
            .verify()
    }

    @Test
    fun givenUserWithAdminRole_whenQueries_shouldReturnCategories() {
        interceptor.setAuthorities(Roles.ADMIN)

        val categories = listOf(
            ListCategoryOutput.from(Fixture.Categories.aulas),
            ListCategoryOutput.from(Fixture.Categories.lives)
        )

        val expectedIds = categories.map { it.id }

        every {
            listCategoryUseCase.execute(any())
        } returns Pagination(0, 10, categories.size.toLong(), categories)

        val document = "query { categories { id } }"

        val graphQlTester = WebGraphQlTester.create(graphqlHandler)

        graphQlTester.document(document).execute().errors().verify()
            .path("categories[*].id").entityList(String::class.java).isEqualTo<GraphQlTester.EntityList<String>>(expectedIds)
    }

    @Test
    fun givenUserWithSubscriberRole_whenQueries_shouldReturnCategories() {
        interceptor.setAuthorities(Roles.SUBSCRIBER)

        val categories = listOf(
            ListCategoryOutput.from(Fixture.Categories.aulas),
            ListCategoryOutput.from(Fixture.Categories.lives)
        )

        val expectedIds = categories.map { it.id }

        every {
            listCategoryUseCase.execute(any())
        } returns Pagination(0, 10, categories.size.toLong(), categories)

        val document = "query { categories { id } }"

        val graphQlTester = WebGraphQlTester.create(graphqlHandler)

        graphQlTester.document(document).execute().errors().verify()
            .path("categories[*].id").entityList(String::class.java).isEqualTo<GraphQlTester.EntityList<String>>(expectedIds)
    }

    @Test
    fun givenUserWithCategoriesRole_whenQueries_shouldReturnCategories() {
        interceptor.setAuthorities(Roles.CATEGORIES)

        val categories = listOf(
            ListCategoryOutput.from(Fixture.Categories.aulas),
            ListCategoryOutput.from(Fixture.Categories.lives)
        )

        val expectedIds = categories.map { it.id }

        every {
            listCategoryUseCase.execute(any())
        } returns Pagination(0, 10, categories.size.toLong(), categories)

        val document = "query { categories { id } }"

        val graphQlTester = WebGraphQlTester.create(graphqlHandler)

        graphQlTester.document(document).execute().errors().verify()
            .path("categories[*].id").entityList(String::class.java).isEqualTo<GraphQlTester.EntityList<String>>(expectedIds)
    }
}
