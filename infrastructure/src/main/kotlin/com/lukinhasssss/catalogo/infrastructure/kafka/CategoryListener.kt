package com.lukinhasssss.catalogo.infrastructure.kafka

import com.fasterxml.jackson.core.type.TypeReference
import com.lukinhasssss.catalogo.application.category.delete.DeleteCategoryUseCase
import com.lukinhasssss.catalogo.application.category.save.SaveCategoryUseCase
import com.lukinhasssss.catalogo.infrastructure.category.CategoryGateway
import com.lukinhasssss.catalogo.infrastructure.category.models.CategoryEvent
import com.lukinhasssss.catalogo.infrastructure.configuration.json.Json
import com.lukinhasssss.catalogo.infrastructure.kafka.models.connect.MessageValue
import com.lukinhasssss.catalogo.infrastructure.kafka.models.connect.Operation
import com.lukinhasssss.catalogo.infrastructure.utils.Logger
import org.springframework.kafka.annotation.DltHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.retry.annotation.Backoff
import org.springframework.stereotype.Component

@Component
class CategoryListener(
    private val categoryGateway: CategoryGateway,
    private val saveCategoryUseCase: SaveCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) {

    companion object {
        private const val TOPIC_RETRY_ATTEMPTS = "4"
        private const val KAFKA_LOG_CODE = "KAFKA"
        private val CATEGORY_MESSAGE = object : TypeReference<MessageValue<CategoryEvent>>() {}
    }

    @KafkaListener(
        concurrency = "\${kafka.consumers.categories.concurrency}",
        containerFactory = "kafkaListenerFactory", // Nome do bean utilizado na classe KafkaConfig
        topics = ["\${kafka.consumers.categories.topics}"],
        groupId = "\${kafka.consumers.categories.group-id}",
        id = "\${kafka.consumers.categories.id}",
        properties = ["auto.offset.reset=\${kafka.consumers.categories.auto-offset-reset}"]
    )
    @RetryableTopic(
        backoff = Backoff(delay = 1000L, multiplier = 2.0),
        attempts = TOPIC_RETRY_ATTEMPTS,
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    fun onMessage(@Payload payload: String, metadata: ConsumerRecordMetadata) {
        loggingForKafkaMessageReceived(payload, metadata)

        val messagePayload = Json.readValue(payload, CATEGORY_MESSAGE).payload
        val operation = messagePayload.operation

        if (Operation.isDelete(operation)) {
            deleteCategoryUseCase.execute(messagePayload.before?.id)
        } else {
            categoryGateway.categoryOfId(messagePayload.after?.id).let {
                saveCategoryUseCase.execute(it)
            }
        }
    }

    @DltHandler
    fun onDltMessage(@Payload payload: String, metadata: ConsumerRecordMetadata) =
        loggingForKafkaDlt(payload, metadata)

    private fun loggingForKafkaMessageReceived(payload: String, metadata: ConsumerRecordMetadata) =
        Logger.info(
            logCode = KAFKA_LOG_CODE,
            message = "Message received from Kafka",
            payload = kafkaLogObject(payload, metadata)
        )

    private fun loggingForKafkaDlt(payload: String, metadata: ConsumerRecordMetadata) =
        Logger.warning(
            logCode = KAFKA_LOG_CODE,
            message = "Message received from Kafka at DLT",
            payload = kafkaLogObject(payload, metadata)
        )

    private fun kafkaLogObject(payload: String, metadata: ConsumerRecordMetadata) = with(metadata) {
        Json.readValue(payload, Map::class.java).toMutableMap().let {
            it.remove("schema")
            it["topic"] = topic()
            it["partition"] = partition()
            it["offset"] = offset()
            it
        }
    }
}
