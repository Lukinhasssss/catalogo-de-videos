package com.lukinhasssss.catalogo

import com.lukinhasssss.catalogo.infrastructure.configuration.WebServerConfig
import com.lukinhasssss.catalogo.infrastructure.kafka.models.connect.Source
import io.mockk.clearAllMocks
import org.apache.kafka.clients.admin.AdminClient
import org.apache.kafka.clients.admin.AdminClientConfig
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

    protected lateinit var producer: Producer<String, String>

    protected lateinit var admin: AdminClient

    @Autowired
    protected lateinit var kafkaBroker: EmbeddedKafkaBroker

    @BeforeAll
    fun init() {
        clearAllMocks()
        admin = AdminClient.create(mapOf(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaBroker.brokersAsString))
        producer = DefaultKafkaProducerFactory(KafkaTestUtils.producerProps(kafkaBroker), StringSerializer(), StringSerializer()).createProducer()
    }

    @AfterAll
    fun shutdown() = producer.close()

    protected fun source(tableName: String) = Source(
        name = "adm_videos_postgresql",
        database = "adm_videos",
        table = tableName
    )
}
