package com.lukinhasssss.catalogo.infrastructure

import org.junit.jupiter.api.Tag
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.core.annotation.AliasFor
import org.springframework.test.context.ActiveProfiles
import java.lang.annotation.Inherited
import kotlin.reflect.KClass

@ActiveProfiles("test-integration")
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Inherited
@GraphQlTest
@Tag(value = "integrationTest")
annotation class GraphQLControllerTest(
    @get:AliasFor(annotation = GraphQlTest::class, attribute = "controllers")
    val controllers: Array<KClass<*>> = []
)
