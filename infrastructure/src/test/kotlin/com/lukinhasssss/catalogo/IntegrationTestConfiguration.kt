package com.lukinhasssss.catalogo

import com.lukinhasssss.catalogo.infrastructure.category.persistence.CategoryRepository
import io.mockk.mockk
import org.springframework.context.annotation.Bean

class IntegrationTestConfiguration {

    @Bean
    fun categoryRepository() = mockk<CategoryRepository>()
}
