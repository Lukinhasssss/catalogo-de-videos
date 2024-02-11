package com.lukinhasssss.catalogo.domain.exception

open class InternalErrorException(
    override val message: String,
    val throwable: Throwable? = null
) : NoStacktraceException(message, throwable) {

    companion object {
        fun with(message: String) = InternalErrorException(message = message)

        fun with(message: String, throwable: Throwable) =
            InternalErrorException(message = message, throwable = throwable)
    }
}
