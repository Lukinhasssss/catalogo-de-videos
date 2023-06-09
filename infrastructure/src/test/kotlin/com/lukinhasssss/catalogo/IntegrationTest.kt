package com.lukinhasssss.catalogo

import com.lukinhasssss.catalogo.infrastructure.configuration.WebServerConfig
import org.junit.jupiter.api.Tag
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import java.lang.annotation.Inherited

@ActiveProfiles("test-integration")
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@SpringBootTest(classes = [WebServerConfig::class])
@Tag(value = "integrationTest")
annotation class IntegrationTest
