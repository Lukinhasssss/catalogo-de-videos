package com.lukinhasssss.catalogo.application

abstract class UnitUseCase<IN> {
    abstract fun execute(anIn: IN)
}
