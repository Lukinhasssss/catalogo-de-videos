package com.lukinhasssss.catalogo.infrastructure.kafka

import com.fasterxml.jackson.core.type.TypeReference
import com.lukinhasssss.catalogo.application.genre.delete.DeleteGenreUseCase
import com.lukinhasssss.catalogo.application.genre.save.SaveGenreUseCase
import com.lukinhasssss.catalogo.infrastructure.configuration.json.Json
import com.lukinhasssss.catalogo.infrastructure.genre.GenreGateway
import com.lukinhasssss.catalogo.infrastructure.genre.models.GenreEvent
import com.lukinhasssss.catalogo.infrastructure.kafka.models.connect.MessageValue
import com.lukinhasssss.catalogo.infrastructure.kafka.models.connect.Operation
import com.lukinhasssss.catalogo.infrastructure.utils.loggingForKafkaDlt
import com.lukinhasssss.catalogo.infrastructure.utils.loggingForKafkaMessageReceived
import org.springframework.kafka.annotation.DltHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.retry.annotation.Backoff
import org.springframework.stereotype.Component

@Component
class GenreListener(
    private val genreGateway: GenreGateway,
    private val saveGenreUseCase: SaveGenreUseCase,
    private val deleteGenreUseCase: DeleteGenreUseCase
) {

    companion object {
        private val GENRE_MESSAGE_TYPE = object : TypeReference<MessageValue<GenreEvent>>() {}
    }

    @KafkaListener(
        concurrency = "\${kafka.consumers.genres.concurrency}",
        containerFactory = "kafkaListenerFactory", // Nome do bean utilizado na classe KafkaConfig
        topics = ["\${kafka.consumers.genres.topics}"],
        groupId = "\${kafka.consumers.genres.group-id}",
        id = "\${kafka.consumers.genres.id}",
        properties = ["auto.offset.reset=\${kafka.consumers.genres.auto-offset-reset}"]
    )
    @RetryableTopic(
        backoff = Backoff(delay = 1000L, multiplier = 2.0),
        attempts = "\${kafka.consumers.genres.max-attempts}",
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    fun onMessage(@Payload payload: String, metadata: ConsumerRecordMetadata) {
        loggingForKafkaMessageReceived(payload, metadata)

        val messagePayload = Json.readValue(payload, GENRE_MESSAGE_TYPE).payload
        val operation = messagePayload.operation

        if (Operation.isDelete(operation)) {
            deleteGenreUseCase.execute(DeleteGenreUseCase.Input(messagePayload.before?.id))
        } else {
            genreGateway.genreOfId(messagePayload.after?.id)?.run {
                val input = SaveGenreUseCase.Input(id, name, active, categories, createdAt, updatedAt, deletedAt)
                saveGenreUseCase.execute(input)
            }
        }
    }

    @DltHandler
    fun onDltMessage(@Payload payload: String, metadata: ConsumerRecordMetadata) {
        loggingForKafkaDlt(payload, metadata)
    }
}
