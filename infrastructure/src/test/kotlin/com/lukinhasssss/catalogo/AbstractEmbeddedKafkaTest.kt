package com.lukinhasssss.catalogo

import com.lukinhasssss.catalogo.infrastructure.configuration.WebServerConfig
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.test.EmbeddedKafkaBroker
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles("test-integration")
@Tag("integrationTest")
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092"])
@EnableAutoConfiguration(exclude = [ ElasticsearchRepositoriesAutoConfiguration::class ])
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(
    classes = [WebServerConfig::class, IntegrationTestConfiguration::class],
    properties = ["kafka.bootstrap-servers=\${spring.embedded.kafka.brokers}"]
)
abstract class AbstractEmbeddedKafkaTest {

    @Autowired
    protected lateinit var kafkaBroker: EmbeddedKafkaBroker

    protected lateinit var producer: Producer<String, String>

    @BeforeAll
    fun init() {
        producer = DefaultKafkaProducerFactory(KafkaTestUtils.producerProps(kafkaBroker), StringSerializer(), StringSerializer()).createProducer()
    }

    @AfterAll
    fun shutdown() = producer.close()
}
