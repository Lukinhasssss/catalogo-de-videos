package com.lukinhasssss.catalogo

import com.lukinhasssss.catalogo.infrastructure.configuration.WebServerConfig
import org.junit.jupiter.api.Tag
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.lang.annotation.Inherited

@ActiveProfiles("test-integration")
@Tag(value = "integrationTest")
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@SpringBootTest(classes = [WebServerConfig::class, IntegrationTestConfiguration::class])
@EnableAutoConfiguration(
    exclude = [
        ElasticsearchRepositoriesAutoConfiguration::class
    ]
)
annotation class IntegrationTest
