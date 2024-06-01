package com.lukinhasssss.catalogo.infrastructure.kafka

import com.lukinhasssss.catalogo.AbstractEmbeddedKafkaTest
import com.lukinhasssss.catalogo.application.genre.delete.DeleteGenreUseCase
import com.lukinhasssss.catalogo.application.genre.save.SaveGenreUseCase
import com.lukinhasssss.catalogo.domain.Fixture.Genres.tech
import com.lukinhasssss.catalogo.infrastructure.configuration.json.Json
import com.lukinhasssss.catalogo.infrastructure.genre.GenreClient
import com.lukinhasssss.catalogo.infrastructure.genre.models.GenreDTO
import com.lukinhasssss.catalogo.infrastructure.genre.models.GenreEvent
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

class GenreListenerTest : AbstractEmbeddedKafkaTest() {

    @MockkBean
    private lateinit var deleteGenreUseCase: DeleteGenreUseCase

    @MockkBean
    private lateinit var saveGenreUseCase: SaveGenreUseCase

    @MockkBean
    private lateinit var genreClient: GenreClient

    @SpykBean
    private lateinit var genreListener: GenreListener

    @Value("\${kafka.consumers.genres.topics}")
    private lateinit var genreTopics: String

    @Test
    fun testGenresTopics() {
        // given
        val expectedMainTopic = "adm_videos_postgresql.public.genres"
        val expectedRetry0Topic = "adm_videos_postgresql.public.genres-retry-0"
        val expectedRetry1Topic = "adm_videos_postgresql.public.genres-retry-1"
        val expectedRetry2Topic = "adm_videos_postgresql.public.genres-retry-2"
        val expectedDLTTopic = "adm_videos_postgresql.public.genres-dlt"

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
        val expectedMainTopic = "adm_videos_postgresql.public.genres"
        val expectedRetry0Topic = "adm_videos_postgresql.public.genres-retry-0"
        val expectedRetry1Topic = "adm_videos_postgresql.public.genres-retry-1"
        val expectedRetry2Topic = "adm_videos_postgresql.public.genres-retry-2"
        val expectedDLTTopic = "adm_videos_postgresql.public.genres-dlt"

        val tech = tech()
        val techEvent = GenreEvent(tech.id)

        val message = Json.writeValueAsString(MessageValue(ValuePayload(techEvent, techEvent, source("genres"), Operation.DELETE)))

        val latch = CountDownLatch(5)

        every { deleteGenreUseCase.execute(any()) } answers {
            latch.countDown()
            throw RuntimeException("Foi de base!")
        }

        every { genreListener.onDltMessage(any(), any()) } answers {
            latch.countDown()
        }

        // when
        producer.send(ProducerRecord(genreTopics, message)).get(10, TimeUnit.SECONDS)

        assertTrue(latch.await(1, TimeUnit.MINUTES))

        // then
        val allMetas = mutableListOf<ConsumerRecordMetadata>()

        verify(exactly = expectedMaxAttempts) { genreListener.onMessage(message, withArg { allMetas.add(it) }) }

        verify(exactly = expectedMaxDLTAttempts) { genreListener.onDltMessage(message, withArg { allMetas.add(it) }) }

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
        with(tech()) {
            // given
            val techEvent = GenreEvent(id)

            val message = Json.writeValueAsString(MessageValue(ValuePayload(null, techEvent, source("genres"), Operation.CREATE)))

            val latch = CountDownLatch(1)

            every { saveGenreUseCase.execute(any()) } answers { latch.countDown(); SaveGenreUseCase.Output(id) }
            every { genreClient.genreOfId(any()) } returns GenreDTO.from(this)

            // when
            producer.send(ProducerRecord(genreTopics, message)).get(10, TimeUnit.SECONDS)

            assertTrue(latch.await(1, TimeUnit.MINUTES))

            // then
            verify { genreClient.genreOfId(id) }
            verify { saveGenreUseCase.execute(SaveGenreUseCase.Input(id, name, isActive, categories, createdAt, updatedAt, deletedAt)) }
        }
    }

    @Test
    fun givenUpdateOperation_whenProcessGoesOK_shouldEndTheOperation() {
        with(tech()) {
            // given
            val techEvent = GenreEvent(id)

            val message = Json.writeValueAsString(MessageValue(ValuePayload(techEvent, techEvent, source("genres"), Operation.UPDATE)))

            val latch = CountDownLatch(1)

            every { saveGenreUseCase.execute(any()) } answers { latch.countDown(); SaveGenreUseCase.Output(id) }
            every { genreClient.genreOfId(any()) } returns GenreDTO.from(this)

            // when
            producer.send(ProducerRecord(genreTopics, message)).get(10, TimeUnit.SECONDS)

            assertTrue(latch.await(1, TimeUnit.MINUTES))

            // then
            verify { genreClient.genreOfId(id) }
            verify { saveGenreUseCase.execute(SaveGenreUseCase.Input(id, name, isActive, categories, createdAt, updatedAt, deletedAt)) }
        }
    }

    @Test
    fun givenDeleteOperation_whenProcessGoesOK_shouldEndTheOperation() {
        with(tech()) {
            // given
            val techEvent = GenreEvent(id)

            val message = Json.writeValueAsString(MessageValue(ValuePayload(techEvent, null, source("genres"), Operation.DELETE)))

            val latch = CountDownLatch(1)

            every { deleteGenreUseCase.execute(any()) } answers { latch.countDown() }
            every { genreClient.genreOfId(any()) } returns GenreDTO.from(this)

            // when
            producer.send(ProducerRecord(genreTopics, message)).get(10, TimeUnit.SECONDS)

            assertTrue(latch.await(1, TimeUnit.MINUTES))

            // then
            verify { deleteGenreUseCase.execute(DeleteGenreUseCase.Input(id)) }
        }
    }
}
