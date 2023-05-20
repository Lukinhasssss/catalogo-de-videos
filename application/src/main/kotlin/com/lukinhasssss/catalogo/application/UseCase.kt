package com.lukinhasssss.catalogo.application

abstract class UseCase<IN, OUT> {
    abstract fun execute(anIn: IN): OUT
}
