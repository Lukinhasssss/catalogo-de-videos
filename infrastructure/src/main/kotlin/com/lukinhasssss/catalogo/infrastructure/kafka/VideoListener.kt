package com.lukinhasssss.catalogo.infrastructure.kafka

import com.fasterxml.jackson.core.type.TypeReference
import com.lukinhasssss.catalogo.application.video.delete.DeleteVideoUseCase
import com.lukinhasssss.catalogo.application.video.save.SaveVideoUseCase
import com.lukinhasssss.catalogo.infrastructure.configuration.json.Json
import com.lukinhasssss.catalogo.infrastructure.kafka.models.connect.MessageValue
import com.lukinhasssss.catalogo.infrastructure.kafka.models.connect.Operation
import com.lukinhasssss.catalogo.infrastructure.utils.loggingForKafkaDlt
import com.lukinhasssss.catalogo.infrastructure.utils.loggingForKafkaMessageReceived
import com.lukinhasssss.catalogo.infrastructure.video.VideoClient
import com.lukinhasssss.catalogo.infrastructure.video.models.VideoDTO
import com.lukinhasssss.catalogo.infrastructure.video.models.VideoEvent
import org.springframework.kafka.annotation.DltHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.retry.annotation.Backoff
import org.springframework.stereotype.Component

@Component
class VideoListener(
    private val videoClient: VideoClient,
    private val saveVideoUseCase: SaveVideoUseCase,
    private val deleteVideoUseCase: DeleteVideoUseCase
) {

    companion object {
        private val GENRE_MESSAGE_TYPE = object : TypeReference<MessageValue<VideoEvent>>() {}
    }

    @KafkaListener(
        concurrency = "\${kafka.consumers.videos.concurrency}",
        containerFactory = "kafkaListenerFactory", // Nome do bean utilizado na classe KafkaConfig
        topics = ["\${kafka.consumers.videos.topics}"],
        groupId = "\${kafka.consumers.videos.group-id}",
        id = "\${kafka.consumers.videos.id}",
        properties = ["auto.offset.reset=\${kafka.consumers.videos.auto-offset-reset}"]
    )
    @RetryableTopic(
        backoff = Backoff(delay = 1000L, multiplier = 2.0),
        attempts = "\${kafka.consumers.videos.max-attempts}",
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    fun onMessage(@Payload payload: String, metadata: ConsumerRecordMetadata) {
        loggingForKafkaMessageReceived(payload, metadata)

        val messagePayload = Json.readValue(payload, GENRE_MESSAGE_TYPE).payload
        val operation = messagePayload.operation

        if (Operation.isDelete(operation)) {
            deleteVideoUseCase.execute(DeleteVideoUseCase.Input(messagePayload.before?.id))
        } else {
            videoClient.videoOfId(messagePayload.after?.id)?.let {
                saveVideoUseCase.execute(it.toSaveVideoInput())
            }
        }
    }

    @DltHandler
    fun onDltMessage(@Payload payload: String, metadata: ConsumerRecordMetadata) {
        loggingForKafkaDlt(payload, metadata)
    }

    private fun VideoDTO.toSaveVideoInput() = SaveVideoUseCase.Input(
        id = id,
        title = title,
        description = description,
        launchedAt = yearLaunched,
        rating = rating,
        duration = duration,
        opened = opened,
        published = published,
        banner = banner?.location,
        thumbnail = thumbnail?.location,
        thumbnailHalf = thumbnailHalf?.location,
        trailer = trailer?.encodedLocation,
        video = video?.encodedLocation,
        categories = categoriesId,
        castMembers = castMembersId,
        genres = genresId,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
