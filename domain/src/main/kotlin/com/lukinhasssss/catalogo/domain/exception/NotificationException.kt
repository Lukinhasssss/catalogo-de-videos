package com.lukinhasssss.catalogo.domain.exception

import com.lukinhasssss.catalogo.domain.validation.Error
import com.lukinhasssss.catalogo.domain.validation.handler.Notification

class NotificationException(
    message: String = "",
    notification: Notification
) : DomainException(message = message, errors = notification.getErrors()) {

    companion object {
        fun with(message: String, notification: Notification): NotificationException {
            return NotificationException(message, notification)
        }

        fun with(anError: Error): DomainException {
            return DomainException(message = anError.message, errors = listOf(anError))
        }

        fun with(anErrors: List<Error>): DomainException {
            return DomainException(message = "", errors = anErrors)
        }

        // fun with(anError: Error): NotificationException {
        //     return NotificationException(message = anError.message!!, Notification.create(anError))
        // }

        // fun with(anErrors: List<Error>): NotificationException {
        //     return NotificationException(message = "", Notification(anErrors.toMutableList()))
        // }
    }
}
