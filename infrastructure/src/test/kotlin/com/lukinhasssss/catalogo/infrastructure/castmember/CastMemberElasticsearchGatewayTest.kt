package com.lukinhasssss.catalogo.infrastructure.castmember

import com.lukinhasssss.catalogo.AbstractElasticsearchTest
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.infrastructure.castmember.persistence.CastMemberRepository
import org.junit.jupiter.api.Assertions.assertEquals
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
}
