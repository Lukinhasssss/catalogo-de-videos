package com.lukinhasssss.catalogo.infrastructure.castmember.models

import com.lukinhasssss.catalogo.domain.UnitTest
import com.lukinhasssss.catalogo.domain.castmember.CastMember
import com.lukinhasssss.catalogo.domain.castmember.CastMemberType
import com.lukinhasssss.catalogo.domain.utils.InstantUtils
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class CastMemberEventTest : UnitTest() {

    @Test
    fun givenCastMemberEvent_whenCallsToCastMember_shouldReturnCastMember() {
        // given
        val expectedId = "1"
        val expectedName = "Any Name"
        val expectedType = "DIRECTOR"
        val expectedCreatedAt = 1630000000000
        val expectedUpdatedAt = 1630000000000

        val castMemberEvent = CastMemberEvent(
            id = expectedId,
            name = expectedName,
            type = expectedType,
            createdAt = expectedCreatedAt,
            updatedAt = expectedUpdatedAt
        )

        // when
        val castMember = castMemberEvent.toCastMember()

        // then
        assertEquals(expectedId, castMember.id)
        assertEquals(expectedName, castMember.name)
        assertEquals(expectedType, castMember.type.name)
        assertEquals(expectedCreatedAt, castMember.createdAt.toEpochMilli())
        assertEquals(expectedUpdatedAt, castMember.updatedAt.toEpochMilli())
    }

    @Test
    fun givenCastMember_whenCallsFrom_shouldReturnCastMemberEvent() {
        // given
        val expectedId = "1"
        val expectedName = "Any Name"
        val expectedType = CastMemberType.DIRECTOR
        val expectedCreatedAt = InstantUtils.now().toEpochMilli()
        val expectedUpdatedAt = InstantUtils.now().toEpochMilli()

        val castMember = CastMember(
            id = expectedId,
            name = expectedName,
            type = expectedType,
            createdAt = InstantUtils.now(),
            updatedAt = InstantUtils.now()
        )

        // when
        val castMemberEvent = CastMemberEvent.from(castMember)

        // then
        assertEquals(expectedId, castMemberEvent.id)
        assertEquals(expectedName, castMemberEvent.name)
        assertEquals(expectedType.name, castMemberEvent.type)
        assertEquals(expectedCreatedAt, castMemberEvent.createdAt)
        assertEquals(expectedUpdatedAt, castMemberEvent.updatedAt)
    }
}
