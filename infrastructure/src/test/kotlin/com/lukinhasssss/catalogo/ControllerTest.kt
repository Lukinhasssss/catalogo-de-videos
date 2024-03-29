package com.lukinhasssss.catalogo

import org.junit.jupiter.api.Tag
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.core.annotation.AliasFor
import org.springframework.test.context.ActiveProfiles
import java.lang.annotation.Inherited
import kotlin.reflect.KClass

@ActiveProfiles("test-integration")
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@WebMvcTest
@Tag(value = "integrationTest")
annotation class ControllerTest(
    @get:AliasFor(annotation = WebMvcTest::class, attribute = "controllers")
    val controllers: Array<KClass<*>> = []
)
