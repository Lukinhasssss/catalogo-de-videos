package com.lukinhasssss.catalogo.infrastructure.castmember

import com.lukinhasssss.catalogo.AbstractElasticsearchTest
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.domain.castmember.CastMemberSearchQuery
import com.lukinhasssss.catalogo.domain.utils.InstantUtils
import com.lukinhasssss.catalogo.infrastructure.castmember.persistence.CastMemberDocument
import com.lukinhasssss.catalogo.infrastructure.castmember.persistence.CastMemberRepository
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertNotNull

class CastMemberElasticsearchGatewayTest : AbstractElasticsearchTest() {

    @Autowired
    private lateinit var castMemberGateway: CastMemberElasticsearchGateway

    @Autowired
    private lateinit var castMemberRepository: CastMemberRepository

    @Test
    fun testInjection() {
        assertNotNull(castMemberGateway)
        assertNotNull(castMemberRepository)
    }

    @Test
    fun givenAValidCastMember_whenCallsSave_shouldPersistIt() {
        // given
        val luffy = Fixture.CastMembers.luffy()

        // when
        val actualOutput = castMemberGateway.save(luffy)

        // then
        assertEquals(luffy, actualOutput)

        castMemberRepository.findById(luffy.id).get().run {
            assertEquals(luffy.id, id)
            assertEquals(luffy.name, name)
            assertEquals(luffy.type, type)
            assertEquals(luffy.createdAt, createdAt)
            assertEquals(luffy.updatedAt, updatedAt)
        }
    }

    @Test
    fun givenAValidId_whenCallsDeleteById_shouldDeleteIt() {
        // given
        val luffy = Fixture.CastMembers.luffy()

        castMemberRepository.save(CastMemberDocument.from(luffy))

        val expectedId = luffy.id

        assertTrue(castMemberRepository.existsById(expectedId))

        // when
        castMemberGateway.deleteById(expectedId)

        // then
        assertFalse(castMemberRepository.existsById(expectedId))
    }

    @Test
    fun givenAnInvalidId_whenCallsDeleteById_shouldBeOk() {
        // when/then
        assertDoesNotThrow { castMemberRepository.deleteById("any") }
    }

    @Test
    fun givenAValidId_whenCallsFindById_shouldRetrieveIt() {
        // given
        val luffy = Fixture.CastMembers.luffy()

        castMemberRepository.save(CastMemberDocument.from(luffy))

        val expectedId = luffy.id

        assertTrue(castMemberRepository.existsById(expectedId))

        // when
        val actualOutput = castMemberGateway.findById(expectedId)

        // then
        with(actualOutput!!) {
            assertEquals(luffy.id, id)
            assertEquals(luffy.name, name)
            assertEquals(luffy.type, type)
            assertEquals(luffy.createdAt, createdAt)
            assertEquals(luffy.updatedAt, updatedAt)
        }
    }

    @Test
    fun givenAnInvalidId_whenCallsFindById_shouldReturnNull() {
        // given
        val expectedId = "any"

        // when
        val actualOutput = castMemberGateway.findById(expectedId)

        // then
        assertNull(actualOutput)
    }

    @Test
    fun givenEmptyCastMembers_whenCallsFindAll_shouldReturnEmptyList() {
        // given
        val expectedPage = 0
        val expectedPerPage = 10
        val expectedTerms = ""
        val expectedSort = "name"
        val expectedDirection = "asc"
        val expectedTotal = 0

        val aQuery = CastMemberSearchQuery(
            page = expectedPage,
            perPage = expectedPerPage,
            terms = expectedTerms,
            sort = expectedSort,
            direction = expectedDirection
        )

        // when
        val actualOutput = castMemberGateway.findAll(aQuery)

        // then
        with(actualOutput) {
            assertEquals(expectedPage, meta.currentPage)
            assertEquals(expectedPerPage, meta.perPage)
            assertEquals(expectedTotal.toLong(), meta.total)
            assertEquals(expectedTotal, data.size)
        }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "luf, 0, 10, 1, 1, Monkey D Luffy",
            "nam, 0, 10, 1, 1, Nami"
        ]
    )
    fun givenValidTerm_whenCallsFindAll_shouldReturnElementsFiltered(
        expectedTerms: String,
        expectedPage: Int,
        expectedPerPage: Int,
        expectedItemsCount: Int,
        expectedTotal: Long,
        expectedName: String
    ) {
        // given
        mockCastMembers()

        val expectedSort = "name"
        val expectedDirection = "asc"

        val aQuery = CastMemberSearchQuery(
            page = expectedPage,
            perPage = expectedPerPage,
            terms = expectedTerms,
            sort = expectedSort,
            direction = expectedDirection
        )

        // when
        val actualOutput = castMemberGateway.findAll(aQuery)

        // then
        with(actualOutput) {
            assertEquals(expectedPage, meta.currentPage)
            assertEquals(expectedPerPage, meta.perPage)
            assertEquals(expectedTotal, meta.total)
            assertEquals(expectedItemsCount, data.size)
            assertEquals(expectedName, data[0].name)
        }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "name, asc, 0, 10, 3, 3, Monkey D Luffy",
            "name, desc, 0, 10, 3, 3, Roronoa Zoro",
            "created_at, asc, 0, 10, 3, 3, Monkey D Luffy",
            "created_at, desc, 0, 10, 3, 3, Nami"
        ]
    )
    fun givenValidSortAndDirection_whenCallsFindAll_shouldReturnElementsSorted(
        expectedSort: String,
        expectedDirection: String,
        expectedPage: Int,
        expectedPerPage: Int,
        expectedItemsCount: Int,
        expectedTotal: Long,
        expectedName: String
    ) {
        // given
        mockCastMembers()

        val expectedTerms = ""

        val aQuery = CastMemberSearchQuery(
            page = expectedPage,
            perPage = expectedPerPage,
            terms = expectedTerms,
            sort = expectedSort,
            direction = expectedDirection
        )

        // when
        val actualOutput = castMemberGateway.findAll(aQuery)

        // then
        with(actualOutput) {
            assertEquals(expectedPage, meta.currentPage)
            assertEquals(expectedPerPage, meta.perPage)
            assertEquals(expectedTotal, meta.total)
            assertEquals(expectedItemsCount, data.size)
            assertEquals(expectedName, data[0].name)
        }
    }

    @ParameterizedTest
    @CsvSource(
        value = [
            "0, 1, 1, 3, Monkey D Luffy",
            "1, 1, 1, 3, Nami",
            // "2, 1, 1, 3, Ronoroa Zoro",
            "3, 1, 0, 3,"
        ]
    )
    fun givenValidPage_whenCallsFindAll_shouldReturnElementsPaged(
        expectedPage: Int,
        expectedPerPage: Int,
        expectedItemsCount: Int,
        expectedTotal: Long,
        expectedName: String?
    ) {
        // given
        mockCastMembers()

        val expectedTerms = ""
        val expectedSort = "name"
        val expectedDirection = "asc"

        val aQuery = CastMemberSearchQuery(
            page = expectedPage,
            perPage = expectedPerPage,
            terms = expectedTerms,
            sort = expectedSort,
            direction = expectedDirection
        )

        // when
        val actualOutput = castMemberGateway.findAll(aQuery)

        // then
        with(actualOutput) {
            assertEquals(expectedPage, meta.currentPage)
            assertEquals(expectedPerPage, meta.perPage)
            assertEquals(expectedTotal, meta.total)
            assertEquals(expectedItemsCount, data.size)

            if (expectedName != null) {
                assertEquals(expectedName, data[0].name)
            }
        }
    }

    @Test
    fun givenValidIds_whenCallsFindAllByIds_shouldReturnElements() {
        // given
        val luffy = castMemberRepository.save(
            CastMemberDocument.from(Fixture.CastMembers.luffy()).copy(createdAt = InstantUtils.now())
        )

        castMemberRepository.save(
            CastMemberDocument.from(Fixture.CastMembers.zoro()).copy(createdAt = InstantUtils.now())
        )

        val nami = castMemberRepository.save(
            CastMemberDocument.from(Fixture.CastMembers.nami()).copy(createdAt = InstantUtils.now())
        )

        val expectedIds = setOf(luffy.id, nami.id)
        val expectedSize = expectedIds.size

        // when
        val actualOutput = castMemberGateway.findAllById(expectedIds)

        // then
        assertEquals(expectedSize, actualOutput.size)
        assertTrue(actualOutput.any { it.id == luffy.id })
        assertTrue(actualOutput.any { it.id == nami.id })
    }

    @Test
    fun givenInvalidIds_whenCallsFindAllByIds_shouldReturnEmptyList() {
        // given
        val luffy = castMemberRepository.save(
            CastMemberDocument.from(Fixture.CastMembers.luffy()).copy(createdAt = InstantUtils.now())
        )

        castMemberRepository.save(
            CastMemberDocument.from(Fixture.CastMembers.zoro()).copy(createdAt = InstantUtils.now())
        )

        val nami = castMemberRepository.save(
            CastMemberDocument.from(Fixture.CastMembers.nami()).copy(createdAt = InstantUtils.now())
        )

        val expectedIds = setOf(luffy.id, "any", nami.id)
        val expectedSize = 2

        // when
        val actualOutput = castMemberGateway.findAllById(expectedIds)

        // then
        assertEquals(expectedSize, actualOutput.size)
        assertTrue(actualOutput.any { it.id == luffy.id })
        assertTrue(actualOutput.any { it.id == nami.id })
    }

    @Test
    fun givenEmptyIds_whenCallsFindAllByIds_shouldReturnEmptyList() {
        // given
        val expectedIds = emptySet<String>()

        // when
        val actualOutput = castMemberGateway.findAllById(expectedIds)

        // then
        assertTrue(actualOutput.isEmpty())
    }

    private fun mockCastMembers() {
        castMemberRepository.save(
            CastMemberDocument.from(Fixture.CastMembers.luffy()).copy(createdAt = InstantUtils.now())
        )
        castMemberRepository.save(
            CastMemberDocument.from(Fixture.CastMembers.zoro()).copy(createdAt = InstantUtils.now())
        )
        castMemberRepository.save(
            CastMemberDocument.from(Fixture.CastMembers.nami()).copy(createdAt = InstantUtils.now())
        )
    }
}
