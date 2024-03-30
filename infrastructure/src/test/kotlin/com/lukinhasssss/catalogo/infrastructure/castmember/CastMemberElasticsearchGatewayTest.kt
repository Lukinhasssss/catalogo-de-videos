package com.lukinhasssss.catalogo.infrastructure.castmember

import com.lukinhasssss.catalogo.AbstractElasticsearchTest
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.infrastructure.castmember.persistence.CastMemberDocument
import com.lukinhasssss.catalogo.infrastructure.castmember.persistence.CastMemberRepository
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
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
}
