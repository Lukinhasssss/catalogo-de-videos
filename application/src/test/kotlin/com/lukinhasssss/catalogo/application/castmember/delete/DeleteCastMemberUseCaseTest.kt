package com.lukinhasssss.catalogo.application.castmember.delete

import com.lukinhasssss.catalogo.application.UseCaseTest
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.domain.castmember.CastMemberGateway
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class DeleteCastMemberUseCaseTest : UseCaseTest() {

    @InjectMockKs
    private lateinit var useCase: DeleteCastMemberUseCase

    @MockK
    private lateinit var castMemberGateway: CastMemberGateway

    @Test
    fun givenAValidId_whenCallsDelete_shouldBeOk() {
        // given
        val aulas = Fixture.CastMembers.zoro()

        val expectedId = aulas.id

        every { castMemberGateway.deleteById(any()) } just Runs

        // when
        assertDoesNotThrow { useCase.execute(expectedId) }

        // then
        verify { castMemberGateway.deleteById(expectedId) }
    }

    @Test
    fun givenAnInvalidId_whenCallsDelete_shouldBeOk() {
        // given
        val expectedId = ""

        // when
        assertDoesNotThrow { useCase.execute(expectedId) }

        // then
        verify(exactly = 0) { castMemberGateway.deleteById(expectedId) }
    }
}
