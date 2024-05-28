package com.lukinhasssss.catalogo.infrastructure.kafka

import com.fasterxml.jackson.core.type.TypeReference
import com.lukinhasssss.catalogo.application.category.delete.DeleteCategoryUseCase
import com.lukinhasssss.catalogo.application.category.save.SaveCategoryUseCase
import com.lukinhasssss.catalogo.infrastructure.category.CategoryClient
import com.lukinhasssss.catalogo.infrastructure.category.models.CategoryEvent
import com.lukinhasssss.catalogo.infrastructure.configuration.json.Json
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
class CategoryListener(
    private val categoryClient: CategoryClient,
    private val saveCategoryUseCase: SaveCategoryUseCase,
    private val deleteCategoryUseCase: DeleteCategoryUseCase
) {

    companion object {
        private val CATEGORY_MESSAGE_TYPE = object : TypeReference<MessageValue<CategoryEvent>>() {}
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
        attempts = "\${kafka.consumers.categories.max-attempts}",
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    fun onMessage(@Payload payload: String, metadata: ConsumerRecordMetadata) {
        loggingForKafkaMessageReceived(payload, metadata)

        val messagePayload = Json.readValue(payload, CATEGORY_MESSAGE_TYPE).payload
        val operation = messagePayload.operation

        if (Operation.isDelete(operation)) {
            deleteCategoryUseCase.execute(messagePayload.before?.id)
        } else {
            categoryClient.categoryOfId(messagePayload.after?.id).let {
                saveCategoryUseCase.execute(it)
            }
        }
    }

    @DltHandler
    fun onDltMessage(@Payload payload: String, metadata: ConsumerRecordMetadata) {
        loggingForKafkaDlt(payload, metadata)

        val messagePayload = Json.readValue(payload, CATEGORY_MESSAGE_TYPE).payload
        val operation = messagePayload.operation

        if (Operation.isDelete(operation)) {
            deleteCategoryUseCase.execute(messagePayload.before?.id)
        } else {
            categoryClient.categoryOfId(messagePayload.after?.id).let {
                saveCategoryUseCase.execute(it)
            }
        }
    }
}
