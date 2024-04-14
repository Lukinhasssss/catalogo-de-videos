package com.lukinhasssss.catalogo.infrastructure.utils

import com.lukinhasssss.catalogo.infrastructure.configuration.json.Json
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata

fun loggingForKafkaMessageReceived(payload: String, metadata: ConsumerRecordMetadata) =
    Logger.info(
        logCode = KAFKA_LOG_CODE,
        message = "Message received from Kafka",
        payload = kafkaLogObject(payload, metadata)
    )

fun loggingForKafkaDlt(payload: String, metadata: ConsumerRecordMetadata) =
    Logger.warning(
        logCode = KAFKA_LOG_CODE,
        message = "Message received from Kafka at DLT",
        payload = kafkaLogObject(payload, metadata)
    )

private const val KAFKA_LOG_CODE = "KAFKA"

private fun kafkaLogObject(payload: String, metadata: ConsumerRecordMetadata) = with(metadata) {
    Json.readValue(payload, Map::class.java).toMutableMap().let {
        it.remove("schema")
        it["topic"] = topic()
        it["partition"] = partition()
        it["offset"] = offset()
        it
    }
}
