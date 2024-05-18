package com.lukinhasssss.catalogo.application

abstract class UseCase<IN, OUT> {
    abstract fun execute(input: IN): OUT
}
