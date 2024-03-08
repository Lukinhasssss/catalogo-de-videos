package com.lukinhasssss.catalogo.infrastructure.configuration

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration(proxyBeanMethods = false)
@ComponentScan("com.lukinhasssss.catalogo")
@EnableScheduling
class WebServerConfig
