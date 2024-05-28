package com.lukinhasssss.catalogo.infrastructure.kafka

import com.lukinhasssss.catalogo.AbstractEmbeddedKafkaTest
import com.lukinhasssss.catalogo.application.video.delete.DeleteVideoUseCase
import com.lukinhasssss.catalogo.application.video.save.SaveVideoUseCase
import com.lukinhasssss.catalogo.domain.Fixture.Videos.cleanCode
import com.lukinhasssss.catalogo.domain.utils.IdUtils
import com.lukinhasssss.catalogo.domain.video.Video
import com.lukinhasssss.catalogo.infrastructure.configuration.json.Json
import com.lukinhasssss.catalogo.infrastructure.kafka.models.connect.MessageValue
import com.lukinhasssss.catalogo.infrastructure.kafka.models.connect.Operation
import com.lukinhasssss.catalogo.infrastructure.kafka.models.connect.ValuePayload
import com.lukinhasssss.catalogo.infrastructure.video.VideoClient
import com.lukinhasssss.catalogo.infrastructure.video.models.ImageResourceDTO
import com.lukinhasssss.catalogo.infrastructure.video.models.VideoDTO
import com.lukinhasssss.catalogo.infrastructure.video.models.VideoEvent
import com.lukinhasssss.catalogo.infrastructure.video.models.VideoResourceDTO
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

class VideoListenerTest : AbstractEmbeddedKafkaTest() {

    @MockkBean
    private lateinit var deleteVideoUseCase: DeleteVideoUseCase

    @MockkBean
    private lateinit var saveVideoUseCase: SaveVideoUseCase

    @MockkBean
    private lateinit var videoClient: VideoClient

    @SpykBean
    private lateinit var videoListener: VideoListener

    @Value("\${kafka.consumers.videos.topics}")
    private lateinit var videoTopics: String

    @Test
    fun testVideosTopics() {
        // given
        val expectedMainTopic = "adm_videos_postgresql.public.videos"
        val expectedRetry0Topic = "adm_videos_postgresql.public.videos-retry-0"
        val expectedRetry1Topic = "adm_videos_postgresql.public.videos-retry-1"
        val expectedRetry2Topic = "adm_videos_postgresql.public.videos-retry-2"
        val expectedDLTTopic = "adm_videos_postgresql.public.videos-dlt"

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
        val expectedMainTopic = "adm_videos_postgresql.public.videos"
        val expectedRetry0Topic = "adm_videos_postgresql.public.videos-retry-0"
        val expectedRetry1Topic = "adm_videos_postgresql.public.videos-retry-1"
        val expectedRetry2Topic = "adm_videos_postgresql.public.videos-retry-2"
        val expectedDLTTopic = "adm_videos_postgresql.public.videos-dlt"

        val cleanCode = cleanCode()
        val cleanCodeEvent = VideoEvent(cleanCode.id)

        val message = Json.writeValueAsString(MessageValue(ValuePayload(cleanCodeEvent, cleanCodeEvent, source, Operation.DELETE)))

        val latch = CountDownLatch(5)

        every { deleteVideoUseCase.execute(any()) } answers {
            latch.countDown()
            throw RuntimeException("Foi de base!")
        }

        every { videoListener.onDltMessage(any(), any()) } answers {
            latch.countDown()
        }

        // when
        producer.send(ProducerRecord(videoTopics, message)).get(10, TimeUnit.SECONDS)

        assertTrue(latch.await(1, TimeUnit.MINUTES))

        // then
        val allMetas = mutableListOf<ConsumerRecordMetadata>()

        verify(exactly = expectedMaxAttempts) { videoListener.onMessage(message, withArg { allMetas.add(it) }) }

        verify(exactly = expectedMaxDLTAttempts) { videoListener.onDltMessage(message, withArg { allMetas.add(it) }) }

        with(allMetas) {
            assertEquals(expectedMainTopic, first().topic())
            assertEquals(expectedRetry0Topic, get(1).topic())
            assertEquals(expectedRetry1Topic, get(2).topic())
            assertEquals(expectedRetry2Topic, get(3).topic())
            assertEquals(expectedDLTTopic, last().topic())
        }
    }

    @Test
    fun givenValidVideo_whenCreateOperationProcessGoesOK_shouldEndTheOperation() {
        with(cleanCode()) {
            // given
            val cleanCodeEvent = VideoEvent(id)

            val message = Json.writeValueAsString(MessageValue(ValuePayload(null, cleanCodeEvent, source, Operation.CREATE)))

            val latch = CountDownLatch(1)

            every { saveVideoUseCase.execute(any()) } answers { latch.countDown(); SaveVideoUseCase.Output(id) }
            every { videoClient.videoOfId(any()) } returns this.toVideoDTO()

            // when
            producer.send(ProducerRecord(videoTopics, message)).get(10, TimeUnit.SECONDS)

            assertTrue(latch.await(1, TimeUnit.MINUTES))

            // then
            verify { videoClient.videoOfId(id) }
            verify {
                saveVideoUseCase.execute(
                    SaveVideoUseCase.Input(
                        id = id,
                        title = title,
                        description = description,
                        launchedAt = launchedAt.value,
                        rating = rating.name,
                        duration = duration,
                        opened = opened,
                        published = published,
                        banner = banner,
                        thumbnail = thumbnail,
                        thumbnailHalf = thumbnailHalf,
                        trailer = trailer,
                        video = video,
                        categories = categories,
                        castMembers = castMembers,
                        genres = genres,
                        createdAt = createdAt.toString(),
                        updatedAt = updatedAt.toString()
                    )
                )
            }
        }
    }

    @Test
    fun givenValidVideo_whenUpdateOperationProcessGoesOK_shouldEndTheOperation() {
        with(cleanCode()) {
            // given
            val cleanCodeEvent = VideoEvent(id)

            val message = Json.writeValueAsString(MessageValue(ValuePayload(cleanCodeEvent, cleanCodeEvent, source, Operation.UPDATE)))

            val latch = CountDownLatch(1)

            every { saveVideoUseCase.execute(any()) } answers { latch.countDown(); SaveVideoUseCase.Output(id) }
            every { videoClient.videoOfId(any()) } returns this.toVideoDTO()

            // when
            producer.send(ProducerRecord(videoTopics, message)).get(10, TimeUnit.SECONDS)

            assertTrue(latch.await(1, TimeUnit.MINUTES))

            // then
            verify { videoClient.videoOfId(id) }
            verify {
                saveVideoUseCase.execute(
                    SaveVideoUseCase.Input(
                        id = id,
                        title = title,
                        description = description,
                        launchedAt = launchedAt.value,
                        rating = rating.name,
                        duration = duration,
                        opened = opened,
                        published = published,
                        banner = banner,
                        thumbnail = thumbnail,
                        thumbnailHalf = thumbnailHalf,
                        trailer = trailer,
                        video = video,
                        categories = categories,
                        castMembers = castMembers,
                        genres = genres,
                        createdAt = createdAt.toString(),
                        updatedAt = updatedAt.toString()
                    )
                )
            }
        }
    }

    @Test
    fun givenDeleteOperation_whenProcessGoesOK_shouldEndTheOperation() {
        with(cleanCode()) {
            // given
            val cleanCodeEvent = VideoEvent(id)

            val message = Json.writeValueAsString(MessageValue(ValuePayload(cleanCodeEvent, null, source, Operation.DELETE)))

            val latch = CountDownLatch(1)

            every { deleteVideoUseCase.execute(any()) } answers { latch.countDown() }
            every { videoClient.videoOfId(any()) } returns this.toVideoDTO()

            // when
            producer.send(ProducerRecord(videoTopics, message)).get(10, TimeUnit.SECONDS)

            assertTrue(latch.await(1, TimeUnit.MINUTES))

            // then
            verify { deleteVideoUseCase.execute(DeleteVideoUseCase.Input(id)) }
        }
    }

    private fun Video.toVideoDTO() = VideoDTO(
        id = id,
        title = title,
        description = description,
        yearLaunched = launchedAt.value,
        rating = rating.name,
        duration = duration,
        opened = opened,
        published = published,
        banner = imageResourceDTO(banner!!),
        thumbnail = imageResourceDTO(thumbnail!!),
        thumbnailHalf = imageResourceDTO(thumbnailHalf!!),
        trailer = videoResourceDTO(trailer!!),
        video = videoResourceDTO(this.video!!),
        categoriesId = categories,
        castMembersId = castMembers,
        genresId = genres,
        createdAt = createdAt.toString(),
        updatedAt = updatedAt.toString()
    )

    private fun videoResourceDTO(data: String) = VideoResourceDTO(
        id = IdUtils.uuid(),
        name = data,
        checksum = IdUtils.uuid(),
        location = data,
        encodedLocation = data,
        status = "processed"
    )

    private fun imageResourceDTO(data: String) = ImageResourceDTO(
        id = IdUtils.uuid(),
        name = data,
        checksum = IdUtils.uuid(),
        location = data
    )
}
