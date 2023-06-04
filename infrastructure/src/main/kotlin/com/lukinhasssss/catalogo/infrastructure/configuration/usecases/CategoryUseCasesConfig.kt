package com.lukinhasssss.catalogo.infrastructure.configuration.usecases

import com.lukinhasssss.catalogo.application.category.delete.DeleteCategoryUseCase
import com.lukinhasssss.catalogo.application.category.list.ListCategoryUseCase
import com.lukinhasssss.catalogo.application.category.save.SaveCategoryUseCase
import com.lukinhasssss.catalogo.domain.category.CategoryGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CategoryUseCasesConfig(
    private val categoryGateway: CategoryGateway
) {

    @Bean
    fun deleteCategoryUseCase() = DeleteCategoryUseCase(categoryGateway)

    @Bean
    fun listCategoryUseCase() = ListCategoryUseCase(categoryGateway)

    @Bean
    fun saveCategoryUseCase() = SaveCategoryUseCase(categoryGateway)
}
