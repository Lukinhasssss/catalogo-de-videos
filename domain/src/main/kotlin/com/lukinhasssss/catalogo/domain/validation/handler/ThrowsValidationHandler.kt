package com.lukinhasssss.catalogo.domain.validation.handler

import com.lukinhasssss.catalogo.domain.exception.DomainException
import com.lukinhasssss.catalogo.domain.validation.Error
import com.lukinhasssss.catalogo.domain.validation.ValidationHandler

class ThrowsValidationHandler : ValidationHandler {

    override fun append(anError: Error): ValidationHandler {
        throw DomainException.with(anError)
    }

    override fun append(anHandler: ValidationHandler): ValidationHandler {
        throw DomainException.with(anHandler.getErrors())
    }

    override fun <T> validate(aValidation: () -> T): T {
        try {
            return aValidation.invoke()
        } catch (ex: Exception) {
            throw DomainException.with(listOf(Error(ex.message)))
        }
    }

    override fun getErrors() = emptyList<Error>()
}
