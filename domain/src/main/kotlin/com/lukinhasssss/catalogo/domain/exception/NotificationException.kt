package com.lukinhasssss.catalogo.domain.exception

import com.lukinhasssss.catalogo.domain.validation.handler.Notification

class NotificationException(
    message: String = "",
    notification: Notification
) : DomainException(message, notification.getErrors())
