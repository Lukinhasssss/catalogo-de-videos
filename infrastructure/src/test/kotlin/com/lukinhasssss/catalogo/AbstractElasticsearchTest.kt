package com.lukinhasssss.catalogo

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.data.elasticsearch.DataElasticsearchTest
import org.springframework.boot.testcontainers.context.ImportTestcontainers
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.junit.jupiter.Testcontainers

@ActiveProfiles("test-integration")
@Tag(value = "integrationTest")
@ComponentScan(
    basePackages = ["com.lukinhasssss.catalogo"],
    useDefaultFilters = false,
    includeFilters = [ComponentScan.Filter(type = FilterType.REGEX, pattern = [".*ElasticsearchGateway"])]
)
@DataElasticsearchTest
@ImportTestcontainers(value = [ElasticsearchTestContainer::class])
@Testcontainers
abstract class AbstractElasticsearchTest {

    companion object {
        @JvmStatic
        @BeforeAll
        fun startElasticsearch(): Unit = ElasticsearchTestContainer.ELASTIC.start()
    }

    @Autowired
    private lateinit var repositories: Collection<ElasticsearchRepository<*, *>>

    @BeforeEach
    fun cleanUp() {
        repositories.forEach { it.deleteAll() }
    }
}
