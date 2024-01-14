package com.lukinhasssss.catalogo.infrastructure

import org.junit.jupiter.api.Tag
import org.springframework.boot.test.autoconfigure.json.JsonTest
import org.springframework.test.context.ActiveProfiles
import java.lang.annotation.Inherited

@ActiveProfiles("test-integration")
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Inherited
@JsonTest
@Tag(value = "integrationTest")
annotation class JacksonTest
