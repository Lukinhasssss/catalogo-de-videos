package com.lukinhasssss.catalogo.infrastructure.exception

import com.lukinhasssss.catalogo.domain.exception.InternalErrorException

data class NotFoundException(override val message: String) : InternalErrorException(message) {
    companion object {
        fun with(message: String) = NotFoundException(message = message)
    }
}
