package com.lukinhasssss.catalogo.infrastructure.configuration

import com.lukinhasssss.catalogo.infrastructure.authentication.RefreshClientCredentials
import org.springframework.context.ApplicationListener
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration(proxyBeanMethods = false)
@ComponentScan("com.lukinhasssss.catalogo")
@EnableScheduling
class WebServerConfig {

    @Bean
    @Profile(value = ["!test-integration && !test-e2e"])
    fun onAppStarted(refreshClientCredentials: RefreshClientCredentials) =
        ApplicationListener<ContextRefreshedEvent> { refreshClientCredentials.refresh() }
}
