package com.lukinhasssss.catalogo

import org.slf4j.LoggerFactory
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy
import org.testcontainers.elasticsearch.ElasticsearchContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import java.util.concurrent.TimeUnit

interface ElasticsearchTestContainer {

    companion object {
        private const val IMAGE = "elasticsearch:7.17.9"
        private const val COMPATIBLE = "docker.elastic.co/elasticsearch/elasticsearch"
        private const val CLUSTER_NAME = "codeflix"
        private const val CLUSTER_USER = "elastic"
        private const val CLUSTER_PWD = "elastic"
        private const val ES_JAVA_OPTS = "-Xms512m -Xmx512m"

        @Container
        val ELASTIC: ElasticsearchContainer =
            ElasticsearchContainer(DockerImageName.parse(IMAGE).asCompatibleSubstituteFor(COMPATIBLE))
                .withExposedPorts(9200, 9300)
                .withPassword(CLUSTER_PWD)
                .withLogConsumer { Slf4jLogConsumer(LoggerFactory.getLogger(ElasticsearchTestContainer::class.java)) }
                .waitingFor(httpWaitStrategy())
                .withEnv(
                    mutableMapOf(
                        "cluster.name" to CLUSTER_NAME,
                        "ES_JAVA_OPTS" to ES_JAVA_OPTS,
                        "http.cors.enabled" to "false",
                        "http.cors.allow-origin" to "*",
                        "xpack.security.enabled" to "false"
                    )
                )

        @JvmStatic
        @DynamicPropertySource
        fun registerElasticsearchUriProperty(registry: DynamicPropertyRegistry) {
            registry.add("elasticsearch.uris") {
                "http://localhost:${ELASTIC.getMappedPort(9200)}"
            }
        }

        private fun httpWaitStrategy(): HttpWaitStrategy {
            return HttpWaitStrategy()
                .forPort(9200)
                .forPath("/")
                .forStatusCode(200)
                .withReadTimeout(Duration.of(5, TimeUnit.MINUTES.toChronoUnit()))
                .withBasicCredentials(CLUSTER_USER, CLUSTER_PWD)
                .allowInsecure()
        }
    }

    // class CatalogoElasticsearchContainer : ElasticsearchContainer(
    //     DockerImageName.parse(IMAGE).asCompatibleSubstituteFor(COMPATIBLE)
    // ) {
    //
    //     companion object {
    //         const val IMAGE = "elasticsearch:7.17.9"
    //         const val COMPATIBLE = "docker.elastic.co/elasticsearch/elasticsearch"
    //         const val CLUSTER_NAME = "codeflix"
    //         const val CLUSTER_USER = "elastic"
    //         const val CLUSTER_PWD = "elastic"
    //         const val ES_JAVA_OPTS = "-Xms512m -Xmx512m"
    //     }
    //
    //     init {
    //         addFixedExposedPort(ELASTIC.getMappedPort(9200), 9200)
    //         addFixedExposedPort(ELASTIC.getMappedPort(9300), 9300)
    //         withLogConsumer(Slf4jLogConsumer(LoggerFactory.getLogger(CatalogoElasticsearchContainer::class.java)))
    //         withPassword(CLUSTER_PWD)
    //         setWaitStrategy(httpWaitStrategy())
    //
    //         withEnv(
    //             mutableMapOf(
    //                 "cluster.name" to CLUSTER_NAME,
    //                 "ES_JAVA_OPTS" to ES_JAVA_OPTS,
    //                 "http.cors.enabled" to "false",
    //                 "http.cors.allow-origin" to "*",
    //                 "xpack.security.enabled" to "false"
    //             )
    //         )
    //     }
    //
    //     private fun httpWaitStrategy(): HttpWaitStrategy {
    //         return HttpWaitStrategy()
    //             .forPort(9200)
    //             .forPath("/")
    //             .forStatusCode(200)
    //             .withReadTimeout(Duration.of(5, TimeUnit.MINUTES.toChronoUnit()))
    //             .withBasicCredentials(CLUSTER_USER, CLUSTER_PWD)
    //             .allowInsecure()
    //     }
    // }
}
