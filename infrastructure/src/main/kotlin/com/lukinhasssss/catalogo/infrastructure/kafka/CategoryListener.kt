package com.lukinhasssss.catalogo.infrastructure.kafka

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
class CategoryListener {

    companion object {
        private const val TOPIC_RETRY_ATTEMPTS = "4"
        private const val KAFKA_LOG_CODE = "KAFKA"
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
        throw RuntimeException("F!")
    }

    @DltHandler
    fun onDltMessage(@Payload payload: String, metadata: ConsumerRecordMetadata) =
        loggingForKafkaDlt(payload, metadata)

    private fun loggingForKafkaMessageReceived(payload: String, metadata: ConsumerRecordMetadata) = with(metadata) {
        Logger.info(
            logCode = KAFKA_LOG_CODE,
            message = "Message received from Kafka",
            payload = mapOf(
                "payload" to payload,
                "topic" to topic(),
                "partition" to partition(),
                "offset" to offset()
            )
        )
    }

    private fun loggingForKafkaDlt(payload: String, metadata: ConsumerRecordMetadata) = with(metadata) {
        Logger.warning(
            logCode = KAFKA_LOG_CODE,
            message = "Message received from Kafka at DLT",
            payload = mapOf(
                "payload" to payload,
                "topic" to topic(),
                "partition" to partition(),
                "offset" to offset()
            )
        )
    }
}
