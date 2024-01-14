package com.lukinhasssss.catalogo

import org.slf4j.LoggerFactory
import org.testcontainers.containers.output.Slf4jLogConsumer
import org.testcontainers.containers.wait.strategy.HttpWaitStrategy
import org.testcontainers.elasticsearch.ElasticsearchContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.utility.DockerImageName
import java.time.Duration
import java.util.concurrent.TimeUnit

interface ElasticsearchTestContainer {

    companion object {
        @JvmStatic
        @Container
        val ELASTIC: ElasticsearchContainer = CatalogoElasticsearchContainer()
    }

    class CatalogoElasticsearchContainer : ElasticsearchContainer(
        DockerImageName.parse(IMAGE).asCompatibleSubstituteFor(COMPATIBLE)
    ) {

        private companion object {
            const val IMAGE = "elasticsearch:8.11.4"
            const val COMPATIBLE = "docker.elastic.co/elasticsearch/elasticsearch"
            const val CLUSTER_NAME = "codeflix"
            const val CLUSTER_USER = "elastic"
            const val CLUSTER_PWD = "elastic"
        }

        init {
            addFixedExposedPort(9200, 9200)
            withLogConsumer(Slf4jLogConsumer(LoggerFactory.getLogger(CatalogoElasticsearchContainer::class.java)))
            withPassword(CLUSTER_PWD)
            setWaitStrategy(httpWaitStrategy())

            envMap["cluster.name"] = CLUSTER_NAME
            envMap["ES_JAVA_OPTS"] = "-Xms512m -Xmx512m"
            // envMap["http.cors.enabled"] = "true"
            // envMap["http.cors.allow-origin"] = "*"
            // envMap["xpack.security.enabled"] = "false"
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
}
