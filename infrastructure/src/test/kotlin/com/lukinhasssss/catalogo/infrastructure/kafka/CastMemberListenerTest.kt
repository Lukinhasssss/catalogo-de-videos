package com.lukinhasssss.catalogo.infrastructure.kafka

import com.lukinhasssss.catalogo.AbstractEmbeddedKafkaTest
import com.lukinhasssss.catalogo.application.castmember.delete.DeleteCastMemberUseCase
import com.lukinhasssss.catalogo.application.castmember.save.SaveCastMemberUseCase
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.infrastructure.castmember.models.CastMemberEvent
import com.lukinhasssss.catalogo.infrastructure.configuration.json.Json
import com.lukinhasssss.catalogo.infrastructure.kafka.models.connect.MessageValue
import com.lukinhasssss.catalogo.infrastructure.kafka.models.connect.Operation
import com.lukinhasssss.catalogo.infrastructure.kafka.models.connect.ValuePayload
import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import io.mockk.every
import io.mockk.verify
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CastMemberListenerTest : AbstractEmbeddedKafkaTest() {

    @MockkBean
    private lateinit var deleteCastMemberUseCase: DeleteCastMemberUseCase

    @MockkBean
    private lateinit var saveCastMemberUseCase: SaveCastMemberUseCase

    @SpykBean
    private lateinit var castMemberListener: CastMemberListener

    @Value("\${kafka.consumers.cast-members.topics}")
    private lateinit var castMemberTopic: String

    @Test
    fun testCastMembersTopics() {
        // given
        val expectedMainTopic = "adm_videos_postgresql.public.cast_members"
        val expectedRetry0Topic = "adm_videos_postgresql.public.cast_members-retry-0"
        val expectedRetry1Topic = "adm_videos_postgresql.public.cast_members-retry-1"
        val expectedRetry2Topic = "adm_videos_postgresql.public.cast_members-retry-2"
        val expectedDLTTopic = "adm_videos_postgresql.public.cast_members-dlt"

        // when
        val actualTopics = admin.listTopics().listings().get().map { it.name() }

        // then
        with(actualTopics) {
            assertTrue(contains(expectedMainTopic))
            assertTrue(contains(expectedRetry0Topic))
            assertTrue(contains(expectedRetry1Topic))
            assertTrue(contains(expectedRetry2Topic))
            assertTrue(contains(expectedDLTTopic))
        }
    }

    @Test
    fun givenInvalidResponsesFromHandler_shouldRetryUntilGoesToDLT() {
        // given
        val expectedMaxAttempts = 4
        val expectedMaxDLTAttempts = 1
        val expectedMainTopic = "adm_videos_postgresql.public.cast_members"
        val expectedRetry0Topic = "adm_videos_postgresql.public.cast_members-retry-0"
        val expectedRetry1Topic = "adm_videos_postgresql.public.cast_members-retry-1"
        val expectedRetry2Topic = "adm_videos_postgresql.public.cast_members-retry-2"
        val expectedDLTTopic = "adm_videos_postgresql.public.cast_members-dlt"

        val nami = Fixture.CastMembers.nami()
        val namiEvent = CastMemberEvent.from(nami)

        val message = Json.writeValueAsString(MessageValue(ValuePayload(namiEvent, namiEvent, source, Operation.DELETE)))

        val latch = CountDownLatch(5)

        every { deleteCastMemberUseCase.execute(any()) } answers {
            latch.countDown()
            throw RuntimeException("Foi de base!")
        }

        every { castMemberListener.onDltMessage(any(), any()) } answers {
            latch.countDown()
        }

        // when
        producer.send(ProducerRecord(castMemberTopic, message)).get(10, TimeUnit.SECONDS)

        assertTrue(latch.await(1, TimeUnit.MINUTES))

        // then
        val allMetas = mutableListOf<ConsumerRecordMetadata>()

        verify(exactly = expectedMaxAttempts) { castMemberListener.onMessage(message, withArg { allMetas.add(it) }) }

        verify(exactly = expectedMaxDLTAttempts) { castMemberListener.onDltMessage(message, withArg { allMetas.add(it) }) }

        with(allMetas) {
            assertEquals(expectedMainTopic, first().topic())
            assertEquals(expectedRetry0Topic, get(1).topic())
            assertEquals(expectedRetry1Topic, get(2).topic())
            assertEquals(expectedRetry2Topic, get(3).topic())
            assertEquals(expectedDLTTopic, last().topic())
        }
    }

    @Test
    fun givenCreateOperation_whenProcessGoesOK_shouldEndTheOperation() {
        // given
        val nami = Fixture.CastMembers.nami()
        val namiEvent = CastMemberEvent.from(nami)

        val message = Json.writeValueAsString(MessageValue(ValuePayload(namiEvent, namiEvent, source, Operation.CREATE)))

        val latch = CountDownLatch(1)

        every { saveCastMemberUseCase.execute(any()) } answers { latch.countDown(); nami }

        // when
        producer.send(ProducerRecord(castMemberTopic, message)).get(10, TimeUnit.SECONDS)

        assertTrue(latch.await(1, TimeUnit.MINUTES))

        // then
        verify { saveCastMemberUseCase.execute(nami) }
    }

    @Test
    fun givenUpdateOperation_whenProcessGoesOK_shouldEndTheOperation() {
        // given
        val nami = Fixture.CastMembers.nami()
        val namiEvent = CastMemberEvent.from(nami)

        val message = Json.writeValueAsString(MessageValue(ValuePayload(namiEvent, namiEvent, source, Operation.UPDATE)))

        val latch = CountDownLatch(1)

        every { saveCastMemberUseCase.execute(any()) } answers { latch.countDown(); nami }

        // when
        producer.send(ProducerRecord(castMemberTopic, message)).get(10, TimeUnit.SECONDS)

        assertTrue(latch.await(1, TimeUnit.MINUTES))

        // then
        verify { saveCastMemberUseCase.execute(nami) }
    }

    @Test
    fun givenDeleteOperation_whenProcessGoesOK_shouldEndTheOperation() {
        // given
        val nami = Fixture.CastMembers.nami()
        val namiEvent = CastMemberEvent.from(nami)

        val message = Json.writeValueAsString(MessageValue(ValuePayload(namiEvent, namiEvent, source, Operation.DELETE)))

        val latch = CountDownLatch(1)

        every { deleteCastMemberUseCase.execute(any()) } answers { latch.countDown() }

        // when
        producer.send(ProducerRecord(castMemberTopic, message)).get(10, TimeUnit.SECONDS)

        assertTrue(latch.await(1, TimeUnit.MINUTES))

        // then
        verify { deleteCastMemberUseCase.execute(nami.id) }
    }
}
