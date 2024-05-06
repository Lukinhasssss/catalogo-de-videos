package com.lukinhasssss.catalogo.domain.castmember

import com.lukinhasssss.catalogo.domain.UnitTest
import com.lukinhasssss.catalogo.domain.exception.DomainException
import com.lukinhasssss.catalogo.domain.utils.IdUtils
import com.lukinhasssss.catalogo.domain.utils.InstantUtils
import com.lukinhasssss.catalogo.domain.validation.handler.ThrowsValidationHandler
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class CastMemberTest : UnitTest() {

    @Test
    fun givenAValidParams_whenCallsWith_thenShouldInstantiateACastMember() {
        // given
        val expectedId = IdUtils.uuid()
        val expectedName = "Paul Walker"
        val expectedType = CastMemberType.ACTOR
        val expectedDate = InstantUtils.now()

        // when
        val actualMember = CastMember.with(
            anId = expectedId,
            aName = expectedName,
            aType = expectedType,
            createdAt = expectedDate,
            updatedAt = expectedDate
        )

        // then
        with(actualMember) {
            assertNotNull(this)
            assertEquals(expectedId, id)
            assertEquals(expectedName, name)
            assertEquals(expectedType, type)
            assertEquals(expectedDate, createdAt)
            assertEquals(expectedDate, updatedAt)
        }
    }

    @Test
    fun givenAValidParams_whenCallsWithCastMember_thenShouldInstantiateACastMember() {
        // given
        val expectedId = IdUtils.uuid()
        val expectedName = "Paul Walker"
        val expectedType = CastMemberType.ACTOR
        val expectedDate = InstantUtils.now()

        val aMember = CastMember.with(
            anId = expectedId,
            aName = expectedName,
            aType = expectedType,
            createdAt = expectedDate,
            updatedAt = expectedDate
        )

        // when
        val actualMember = CastMember.with(aMember)

        // then
        assertEquals(aMember, actualMember)
    }

    @Test
    fun givenAnInvalidId_whenCallsWithAndValidate_thenShouldReceiveError() {
        // given
        val expectedId = ""
        val expectedName = "Paul Walker"
        val expectedType = CastMemberType.ACTOR
        val expectedDate = InstantUtils.now()
        val expectedErrorCount = 1
        val expectedErrorMessage = "'id' should not be empty"

        val actualMember = CastMember.with(
            anId = expectedId,
            aName = expectedName,
            aType = expectedType,
            createdAt = expectedDate,
            updatedAt = expectedDate
        )

        // when
        val actualException = assertThrows<DomainException> {
            actualMember.validate(ThrowsValidationHandler())
        }

        // then
        with(actualException.errors) {
            assertEquals(expectedErrorCount, count())
            assertEquals(expectedErrorMessage, first().message)
        }
    }

    @Test
    fun givenAnInvalidName_whenCallsWithAndValidate_thenShouldReceiveError() {
        // given
        val expectedId = IdUtils.uuid()
        val expectedName = ""
        val expectedType = CastMemberType.ACTOR
        val expectedDate = InstantUtils.now()
        val expectedErrorCount = 1
        val expectedErrorMessage = "'name' should not be empty"

        val actualMember = CastMember.with(
            anId = expectedId,
            aName = expectedName,
            aType = expectedType,
            createdAt = expectedDate,
            updatedAt = expectedDate
        )

        // when
        val actualException = assertThrows<DomainException> {
            actualMember.validate(ThrowsValidationHandler())
        }

        // then
        with(actualException.errors) {
            assertEquals(expectedErrorCount, count())
            assertEquals(expectedErrorMessage, first().message)
        }
    }
}
