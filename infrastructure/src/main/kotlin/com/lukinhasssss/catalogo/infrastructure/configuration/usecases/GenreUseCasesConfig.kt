package com.lukinhasssss.catalogo.infrastructure.configuration.usecases

import com.lukinhasssss.catalogo.application.genre.delete.DeleteGenreUseCase
import com.lukinhasssss.catalogo.application.genre.list.ListGenreUseCase
import com.lukinhasssss.catalogo.application.genre.save.SaveGenreUseCase
import com.lukinhasssss.catalogo.domain.genre.GenreGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class GenreUseCasesConfig(
    private val genreGateway: GenreGateway
) {

    @Bean
    fun deleteGenreUseCase() = DeleteGenreUseCase(genreGateway)

    @Bean
    fun listGenreUseCase() = ListGenreUseCase(genreGateway)

    @Bean
    fun saveGenreUseCase() = SaveGenreUseCase(genreGateway)
}
