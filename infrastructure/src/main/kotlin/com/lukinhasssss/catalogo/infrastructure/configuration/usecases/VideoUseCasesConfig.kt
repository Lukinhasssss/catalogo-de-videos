package com.lukinhasssss.catalogo.infrastructure.configuration.usecases

import com.lukinhasssss.catalogo.application.video.delete.DeleteVideoUseCase
import com.lukinhasssss.catalogo.application.video.get.GetVideoUseCase
import com.lukinhasssss.catalogo.application.video.list.ListVideoUseCase
import com.lukinhasssss.catalogo.application.video.save.SaveVideoUseCase
import com.lukinhasssss.catalogo.domain.video.VideoGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class VideoUseCasesConfig(
    private val videoGateway: VideoGateway
) {

    @Bean
    fun deleteVideoUseCase() = DeleteVideoUseCase(videoGateway)

    @Bean
    fun listVideoUseCase() = ListVideoUseCase(videoGateway)

    @Bean
    fun saveVideoUseCase() = SaveVideoUseCase(videoGateway)

    @Bean
    fun getVideoUseCase() = GetVideoUseCase(videoGateway)
}
