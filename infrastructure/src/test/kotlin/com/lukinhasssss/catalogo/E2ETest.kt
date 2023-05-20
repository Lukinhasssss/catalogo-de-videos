package com.lukinhasssss.catalogo

import com.lukinhasssss.catalogo.infrastructure.configuration.WebServerConfig
import org.junit.jupiter.api.Tag
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import java.lang.annotation.Inherited

@ActiveProfiles("test-e2e")
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@DirtiesContext
@SpringBootTest(
    classes = [WebServerConfig::class],
    webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@Tag(value = "e2eTest")
annotation class E2ETest

/**
 * Test annotation which indicates that the ApplicationContext associated with a test is dirty
 * and should therefore be closed and removed from the context cache.
 **/
