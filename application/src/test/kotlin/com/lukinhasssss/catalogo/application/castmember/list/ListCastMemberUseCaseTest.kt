package com.lukinhasssss.catalogo.application.castmember.list

import com.lukinhasssss.catalogo.application.UseCaseTest
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.domain.castmember.CastMemberGateway
import com.lukinhasssss.catalogo.domain.castmember.CastMemberSearchQuery
import com.lukinhasssss.catalogo.domain.pagination.Pagination
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ListCastMemberUseCaseTest : UseCaseTest() {

    @InjectMockKs
    private lateinit var useCase: ListCastMemberUseCase

    @MockK
    private lateinit var castMemberGateway: CastMemberGateway

    @Test
    fun givenAValidQuery_whenCallsListCastMembers_shouldReturnCategories() {
        // given
        val members = listOf(Fixture.CastMembers.luffy(), Fixture.CastMembers.zoro())

        // val expectedItems = members.map { ListCastMemberOutput.from(it) }
        val expectedItems = members.map(ListCastMemberOutput::from)
        val expectedPage = 0
        val expectedPerPage = 10
        val expectedTerms = "Algo"
        val expectedSort = "name"
        val expectedDirection = "asc"
        val expectedItemsCount = 2

        val aQuery = CastMemberSearchQuery(
            page = expectedPage,
            perPage = expectedPerPage,
            terms = expectedTerms,
            sort = expectedSort,
            direction = expectedDirection
        )

        val pagination = Pagination(
            currentPage = expectedPage,
            perPage = expectedPerPage,
            total = members.size.toLong(),
            items = members
        )

        every { castMemberGateway.findAll(any()) } returns pagination

        // when
        val actualOutput = useCase.execute(aQuery)

        // then
        with(actualOutput) {
            assertEquals(expectedPage, meta.currentPage)
            assertEquals(expectedPerPage, meta.perPage)
            assertEquals(expectedItemsCount, meta.total.toInt())
            assertTrue(expectedItems.size == data.size && expectedItems.containsAll(data))
        }
    }
}
