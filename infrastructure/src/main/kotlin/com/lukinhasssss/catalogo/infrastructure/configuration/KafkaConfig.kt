package com.lukinhasssss.catalogo.infrastructure.configuration

import com.lukinhasssss.catalogo.infrastructure.configuration.properties.KafkaProperties
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.common.serialization.StringDeserializer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.config.KafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer

@Configuration(proxyBeanMethods = false)
class KafkaConfig(
    private val properties: KafkaProperties
) {

    @Bean
    fun kafkaListenerFactory(): KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> =
        ConcurrentKafkaListenerContainerFactory<String, String>().run {
            consumerFactory = consumerFactory()
            containerProperties.pollTimeout = properties.poolTimeout
            this
        }

    private fun consumerFactory(): ConsumerFactory<String, Any> = DefaultKafkaConsumerFactory(consumerConfigs())

    private fun consumerConfigs(): Map<String, Any> = mapOf(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to properties.bootstrapServers,
        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
        ConsumerConfig.ALLOW_AUTO_CREATE_TOPICS_CONFIG to properties.autoCreateTopics
    )
}
