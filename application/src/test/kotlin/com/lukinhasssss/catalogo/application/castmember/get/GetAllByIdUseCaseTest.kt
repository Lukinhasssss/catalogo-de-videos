package com.lukinhasssss.catalogo.application.castmember.get

import com.lukinhasssss.catalogo.application.UseCaseTest
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.domain.castmember.CastMemberGateway
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class GetAllByIdUseCaseTest : UseCaseTest() {

    @InjectMockKs
    private lateinit var useCase: GetAllByIdUseCase

    @MockK
    private lateinit var castMemberGateway: CastMemberGateway

    @Test
    fun givenValidIds_whenCallsGetAllById_shouldReturnIt() {
        // given
        val members = listOf(Fixture.CastMembers.luffy(), Fixture.CastMembers.zoro())

        val expectedItems = members.map { GetAllByIdUseCase.Output(it) }

        val expectedIds = members.map { it.id }

        every { castMemberGateway.findAllById(any()) } returns members

        // when
        val actualOutput = useCase.execute(GetAllByIdUseCase.Input(expectedIds))

        // then
        assertTrue { expectedItems.size == actualOutput.size && expectedItems.containsAll(actualOutput) }

        verify { castMemberGateway.findAllById(expectedIds) }
    }

    @Test
    fun givenInvalidIds_whenCallsGetAllById_shouldReturnEmpty() {
        // given
        val ids = listOf("1", "2", "3")

        every { castMemberGateway.findAllById(any()) } returns emptyList()

        // when
        val actualOutput = useCase.execute(GetAllByIdUseCase.Input(ids))

        // then
        assertTrue { actualOutput.isEmpty() }

        verify { castMemberGateway.findAllById(ids) }
    }

    @Test
    fun givenEmptyIds_whenCallsGetAllById_shouldReturnEmpty() {
        // given
        val ids = emptyList<String>()

        // when
        val actualOutput = useCase.execute(GetAllByIdUseCase.Input(ids))

        // then
        assertTrue { actualOutput.isEmpty() }

        verify(exactly = 0) { castMemberGateway.findAllById(any()) }
    }
}