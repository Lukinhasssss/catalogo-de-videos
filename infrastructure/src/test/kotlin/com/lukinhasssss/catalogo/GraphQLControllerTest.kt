package com.lukinhasssss.catalogo

import com.lukinhasssss.catalogo.infrastructure.configuration.ObjectMapperConfig
import org.junit.jupiter.api.Tag
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.FilterType
import org.springframework.core.annotation.AliasFor
import org.springframework.test.context.ActiveProfiles
import java.lang.annotation.Inherited
import kotlin.reflect.KClass

@ActiveProfiles("test-integration")
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.TYPE, AnnotationTarget.CLASS)
@Inherited
@GraphQlTest(
    excludeFilters = [ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = [ObjectMapperConfig::class])]
)
@Tag(value = "integrationTest")
annotation class GraphQLControllerTest(
    @get:AliasFor(annotation = GraphQlTest::class, attribute = "controllers")
    val controllers: Array<KClass<*>> = []
)
