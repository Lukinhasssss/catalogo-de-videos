package com.lukinhasssss.catalogo.infrastructure.kafka

import com.lukinhasssss.catalogo.AbstractEmbeddedKafkaTest
import com.lukinhasssss.catalogo.application.category.delete.DeleteCategoryUseCase
import com.lukinhasssss.catalogo.application.category.save.SaveCategoryUseCase
import com.lukinhasssss.catalogo.domain.Fixture
import com.lukinhasssss.catalogo.infrastructure.category.CategoryGateway
import com.lukinhasssss.catalogo.infrastructure.category.models.CategoryEvent
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

class CategoryListenerTest : AbstractEmbeddedKafkaTest() {

    @MockkBean
    private lateinit var deleteCategoryUseCase: DeleteCategoryUseCase

    @MockkBean
    private lateinit var saveCategoryUseCase: SaveCategoryUseCase

    @MockkBean
    private lateinit var categoryGateway: CategoryGateway

    @SpykBean
    private lateinit var categoryListener: CategoryListener

    @Value("\${kafka.consumers.categories.topics}")
    private lateinit var categoryTopic: String

    @Test
    fun testCategoriesTopics() {
        // given
        val expectedMainTopic = "adm_videos_postgresql.public.categories"
        val expectedRetry0Topic = "adm_videos_postgresql.public.categories-retry-0"
        val expectedRetry1Topic = "adm_videos_postgresql.public.categories-retry-1"
        val expectedRetry2Topic = "adm_videos_postgresql.public.categories-retry-2"
        val expectedDLTTopic = "adm_videos_postgresql.public.categories-dlt"

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
    fun givenInvalidResponsesFromHandler_shouldRetryUntilGpesToDLT() {
        // given
        val expectedMaxAttempts = 4
        val expectedMaxDLTAttempts = 1
        val expectedMainTopic = "adm_videos_postgresql.public.categories"
        val expectedRetry0Topic = "adm_videos_postgresql.public.categories-retry-0"
        val expectedRetry1Topic = "adm_videos_postgresql.public.categories-retry-1"
        val expectedRetry2Topic = "adm_videos_postgresql.public.categories-retry-2"
        val expectedDLTTopic = "adm_videos_postgresql.public.categories-dlt"

        val aulas = Fixture.Categories.aulas
        val aulasEvent = CategoryEvent(aulas.id)

        val message = Json.writeValueAsString(MessageValue(ValuePayload(aulasEvent, aulasEvent, source, Operation.DELETE)))

        val latch = CountDownLatch(5)

        every { deleteCategoryUseCase.execute(any()) } answers {
            latch.countDown()
            if (latch.count > 0) { throw RuntimeException("Foi de base!") }
        }

        // when
        producer.send(ProducerRecord(categoryTopic, message)).get(10, TimeUnit.SECONDS)

        assertTrue(latch.await(1, TimeUnit.MINUTES))

        // then
        val allMetas = mutableListOf<ConsumerRecordMetadata>()

        verify(exactly = expectedMaxAttempts) { categoryListener.onMessage(message, withArg { allMetas.add(it) }) }

        verify(exactly = expectedMaxDLTAttempts) { categoryListener.onDltMessage(message, withArg { allMetas.add(it) }) }

        with(allMetas) {
            assertEquals(expectedMainTopic, first.topic())
            assertEquals(expectedRetry0Topic, get(1).topic())
            assertEquals(expectedRetry1Topic, get(2).topic())
            assertEquals(expectedRetry2Topic, get(3).topic())
            assertEquals(expectedDLTTopic, last.topic())
        }
    }

    @Test
    fun givenCreateOperation_whenProcessGoesOK_shouldEndTheOperation() {
        // given
        val aulas = Fixture.Categories.aulas
        val aulasEvent = CategoryEvent(aulas.id)

        val message = Json.writeValueAsString(MessageValue(ValuePayload(aulasEvent, aulasEvent, source, Operation.CREATE)))

        val latch = CountDownLatch(1)

        every { saveCategoryUseCase.execute(any()) } answers { latch.countDown(); aulas }
        every { categoryGateway.categoryOfId(any()) } returns aulas

        // when
        producer.send(ProducerRecord(categoryTopic, message)).get(10, TimeUnit.SECONDS)

        assertTrue(latch.await(1, TimeUnit.MINUTES))

        // then
        verify { categoryGateway.categoryOfId(aulas.id) }
        verify { saveCategoryUseCase.execute(aulas) }
    }

    @Test
    fun givenUpdateOperation_whenProcessGoesOK_shouldEndTheOperation() {
        // given
        val aulas = Fixture.Categories.aulas
        val aulasEvent = CategoryEvent(aulas.id)

        val message = Json.writeValueAsString(MessageValue(ValuePayload(aulasEvent, aulasEvent, source, Operation.UPDATE)))

        val latch = CountDownLatch(1)

        every { saveCategoryUseCase.execute(any()) } answers { latch.countDown(); aulas }
        every { categoryGateway.categoryOfId(any()) } returns aulas

        // when
        producer.send(ProducerRecord(categoryTopic, message)).get(10, TimeUnit.SECONDS)

        assertTrue(latch.await(1, TimeUnit.MINUTES))

        // then
        verify { categoryGateway.categoryOfId(aulas.id) }
        verify { saveCategoryUseCase.execute(aulas) }
    }

    @Test
    fun givenDeleteOperation_whenProcessGoesOK_shouldEndTheOperation() {
        // given
        val aulas = Fixture.Categories.aulas
        val aulasEvent = CategoryEvent(aulas.id)

        val message = Json.writeValueAsString(MessageValue(ValuePayload(aulasEvent, aulasEvent, source, Operation.DELETE)))

        val latch = CountDownLatch(1)

        every { deleteCategoryUseCase.execute(any()) } answers { latch.countDown() }

        // when
        producer.send(ProducerRecord(categoryTopic, message)).get(10, TimeUnit.SECONDS)

        assertTrue(latch.await(1, TimeUnit.MINUTES))

        // then
        verify { deleteCategoryUseCase.execute(aulas.id) }
    }
}
