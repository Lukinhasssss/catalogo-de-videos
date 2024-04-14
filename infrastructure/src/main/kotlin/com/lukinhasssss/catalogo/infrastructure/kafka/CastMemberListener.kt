package com.lukinhasssss.catalogo.infrastructure.kafka

import com.fasterxml.jackson.core.type.TypeReference
import com.lukinhasssss.catalogo.application.castmember.delete.DeleteCastMemberUseCase
import com.lukinhasssss.catalogo.application.castmember.save.SaveCastMemberUseCase
import com.lukinhasssss.catalogo.infrastructure.castmember.models.CastMemberEvent
import com.lukinhasssss.catalogo.infrastructure.configuration.json.Json
import com.lukinhasssss.catalogo.infrastructure.kafka.models.connect.MessageValue
import com.lukinhasssss.catalogo.infrastructure.utils.loggingForKafkaMessageReceived
import org.springframework.kafka.annotation.DltHandler
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.annotation.RetryableTopic
import org.springframework.kafka.listener.adapter.ConsumerRecordMetadata
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Component

@Component
class CastMemberListener(
    private val saveCastMemberUseCase: SaveCastMemberUseCase,
    private val deleteCastMemberUseCase: DeleteCastMemberUseCase
) {

    companion object {
        private val CAST_MEMBER_MESSAGE = object : TypeReference<MessageValue<CastMemberEvent>>() {}
    }

    @KafkaListener(
        concurrency = "\${kafka.consumers.cast-members.concurrency}",
        containerFactory = "kafkaListenerFactory", // Nome do bean utilizado na classe KafkaConfig
        topics = ["\${kafka.consumers.cast-members.topics}"],
        groupId = "\${kafka.consumers.cast-members.group-id}",
        id = "\${kafka.consumers.cast-members.id}",
        properties = ["auto.offset.reset=\${kafka.consumers.cast-members.auto-offset-reset}"]
    )
    @RetryableTopic(
        attempts = "\${kafka.consumers.cast-members.max-attempts}",
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    fun onMessage(@Payload payload: String, metadata: ConsumerRecordMetadata) {
        loggingForKafkaMessageReceived(payload, metadata)

        val messagePayload = Json.readValue(payload, CAST_MEMBER_MESSAGE).payload
        val operation = messagePayload.operation

        if (operation.isDelete()) {
            deleteCastMemberUseCase.execute(messagePayload.before?.id)
        } else {
            saveCastMemberUseCase.execute(messagePayload.after?.toCastMember())
        }
    }

    @DltHandler
    fun onDltMessage(@Payload payload: String, metadata: ConsumerRecordMetadata) =
        loggingForKafkaMessageReceived(payload, metadata)
}
