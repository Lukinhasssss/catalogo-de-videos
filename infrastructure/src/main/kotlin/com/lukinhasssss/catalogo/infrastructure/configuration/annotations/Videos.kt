package com.lukinhasssss.catalogo.infrastructure.configuration.annotations

import org.springframework.beans.factory.annotation.Qualifier

@Qualifier("Video")
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class Videos
