package com.lukinhasssss.catalogo.domain.validation

interface ValidationHandler {

    fun append(anError: Error): ValidationHandler

    fun append(anHandler: ValidationHandler): ValidationHandler

    fun <T> validate(aValidation: () -> T): T?

    fun getErrors(): List<Error>

    fun hasError(): Boolean = getErrors().isNotEmpty()

    fun firstError(): Error = getErrors()[0]

    interface Validation<T> {
        fun validate(): T
    }
}
