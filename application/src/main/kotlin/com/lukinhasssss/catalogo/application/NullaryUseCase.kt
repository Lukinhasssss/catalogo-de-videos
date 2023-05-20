package com.lukinhasssss.catalogo.application

abstract class NullaryUseCase<OUT> {
    abstract fun execute(): OUT
}
