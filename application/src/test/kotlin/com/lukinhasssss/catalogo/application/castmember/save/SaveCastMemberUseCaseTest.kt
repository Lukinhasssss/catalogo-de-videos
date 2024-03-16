package com.lukinhasssss.catalogo.application.castmember.save

import com.lukinhasssss.catalogo.application.UseCaseTest
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.domain.castmember.CastMember
import com.lukinhasssss.catalogo.domain.castmember.CastMemberGateway
import com.lukinhasssss.catalogo.domain.exception.DomainException
import io.mockk.Called
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class SaveCastMemberUseCaseTest : UseCaseTest() {

    @InjectMockKs
    private lateinit var useCase: SaveCastMemberUseCase

    @MockK
    private lateinit var castMemberGateway: CastMemberGateway

    @Test
    fun givenAValidCastMember_whenCallsSave_shouldPersistIt() {
        // given
        val aMember = Fixture.CastMembers.luffy()

        every { castMemberGateway.save(any()) } answers { firstArg() }

        // when
        useCase.execute(aMember)

        // then
        verify { castMemberGateway.save(aMember) }
    }

    @Test
    fun givenANullCastMember_whenCallsSave_shouldReturnError() {
        // given
        val aMember: CastMember? = null
        val expectedErrorCount = 1
        val expectedErrorMessage = "A member cannot be null"

        // when
        val actualError = assertThrows<DomainException> { useCase.execute(aMember) }

        // then
        assertEquals(expectedErrorCount, actualError.errors.size)
        assertEquals(expectedErrorMessage, actualError.errors.first().message)

        verify { castMemberGateway.save(any()) wasNot Called }
    }

    @Test
    fun givenAnInvalidId_whenCallsSave_shouldReturnError() {
        // given
        val expectedErrorCount = 1
        val expectedErrorMessage = "'id' should not be empty"

        val aMember = Fixture.CastMembers.luffy().copy(id = "")

        // when
        val actualError = assertThrows<DomainException> { useCase.execute(aMember) }

        // then
        assertEquals(expectedErrorCount, actualError.errors.size)
        assertEquals(expectedErrorMessage, actualError.errors.first().message)

        verify { castMemberGateway.save(any()) wasNot Called }
    }

    @Test
    fun givenAnInvalidName_whenCallsSave_shouldReturnError() {
        // given
        val expectedErrorCount = 1
        val expectedErrorMessage = "'name' should not be empty"

        val aMember = Fixture.CastMembers.zoro().copy(name = "")

        // when
        val actualError = assertThrows<DomainException> { useCase.execute(aMember) }

        // then
        assertEquals(expectedErrorCount, actualError.errors.size)
        assertEquals(expectedErrorMessage, actualError.errors.first().message)

        verify { castMemberGateway.save(any()) wasNot Called }
    }
}
