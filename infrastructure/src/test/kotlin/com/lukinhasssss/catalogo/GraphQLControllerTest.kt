package com.lukinhasssss.catalogo

import com.lukinhasssss.catalogo.infrastructure.configuration.ObjectMapperConfig
import org.junit.jupiter.api.Tag
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.context.annotation.Import
import org.springframework.core.annotation.AliasFor
import org.springframework.test.context.ActiveProfiles
import java.lang.annotation.Inherited
import kotlin.reflect.KClass

@ActiveProfiles("test-integration")
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Inherited
@GraphQlTest
@Import(ObjectMapperConfig::class)
@Tag(value = "integrationTest")
annotation class GraphQLControllerTest(
    @get:AliasFor(annotation = GraphQlTest::class, attribute = "controllers")
    val controllers: Array<KClass<*>> = []
)
