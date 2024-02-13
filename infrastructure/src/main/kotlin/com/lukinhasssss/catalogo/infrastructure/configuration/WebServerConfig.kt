package com.lukinhasssss.catalogo.infrastructure.configuration

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration(proxyBeanMethods = false)
@ComponentScan("com.lukinhasssss.catalogo")
class WebServerConfig
