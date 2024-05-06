package com.lukinhasssss.catalogo.infrastructure.castmember.models

import com.lukinhasssss.catalogo.domain.UnitTest
import com.lukinhasssss.catalogo.domain.castmember.CastMember
import com.lukinhasssss.catalogo.domain.castmember.CastMemberType
import com.lukinhasssss.catalogo.domain.utils.InstantUtils
import org.junit.jupiter.api.Test
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals

class CastMemberEventTest : UnitTest() {

    @Test
    fun givenCastMemberEvent_whenCallsToCastMember_shouldReturnCastMember() {
        // given
        val expectedId = "1"
        val expectedName = "Any Name"
        val expectedType = "DIRECTOR"
        val unixTimestamp = 1714313893842354
        val expectedDates = InstantUtils.fromTimestamp(unixTimestamp)

        val castMemberEvent = CastMemberEvent(
            id = expectedId,
            name = expectedName,
            type = expectedType,
            createdAt = unixTimestamp,
            updatedAt = unixTimestamp
        )

        // when
        val castMember = castMemberEvent.toCastMember()

        // then
        assertEquals(expectedId, castMember.id)
        assertEquals(expectedName, castMember.name)
        assertEquals(expectedType, castMember.type.name)
        assertEquals(expectedDates, castMember.createdAt.truncatedTo(ChronoUnit.MILLIS))
        assertEquals(expectedDates, castMember.updatedAt.truncatedTo(ChronoUnit.MILLIS))
    }

    @Test
    fun givenCastMember_whenCallsFrom_shouldReturnCastMemberEvent() {
        // given
        val expectedId = "1"
        val expectedName = "Any Name"
        val expectedType = CastMemberType.DIRECTOR
        val expectedCreatedAt = InstantUtils.now()
        val expectedUpdatedAt = InstantUtils.now()

        val castMember = CastMember(
            id = expectedId,
            name = expectedName,
            type = expectedType,
            createdAt = expectedCreatedAt,
            updatedAt = expectedUpdatedAt
        )

        // when
        val castMemberEvent = CastMemberEvent.from(castMember)

        // then
        assertEquals(expectedId, castMemberEvent.id)
        assertEquals(expectedName, castMemberEvent.name)
        assertEquals(expectedType.name, castMemberEvent.type)
        assertEquals(expectedCreatedAt.toEpochMilli(), castMemberEvent.createdAt)
        assertEquals(expectedUpdatedAt.toEpochMilli(), castMemberEvent.updatedAt)
    }
}
