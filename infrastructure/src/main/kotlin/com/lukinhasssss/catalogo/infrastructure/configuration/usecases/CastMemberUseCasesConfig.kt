package com.lukinhasssss.catalogo.infrastructure.configuration.usecases

import com.lukinhasssss.catalogo.application.castmember.delete.DeleteCastMemberUseCase
import com.lukinhasssss.catalogo.application.castmember.list.ListCastMemberUseCase
import com.lukinhasssss.catalogo.application.castmember.save.SaveCastMemberUseCase
import com.lukinhasssss.catalogo.domain.castmember.CastMemberGateway
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
class CastMemberUseCasesConfig(
    private val castMemberGateway: CastMemberGateway
) {

    @Bean
    fun saveCastMemberUseCase() = SaveCastMemberUseCase(castMemberGateway)

    @Bean
    fun listCastMemberUseCase() = ListCastMemberUseCase(castMemberGateway)

    @Bean
    fun deleteCastMemberUseCase() = DeleteCastMemberUseCase(castMemberGateway)
}
