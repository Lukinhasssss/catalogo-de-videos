package com.lukinhasssss.catalogo.application.castmember.save

import com.lukinhasssss.catalogo.application.UseCase
import com.lukinhasssss.catalogo.domain.castmember.CastMember
import com.lukinhasssss.catalogo.domain.castmember.CastMemberGateway
import com.lukinhasssss.catalogo.domain.exception.NotificationException
import com.lukinhasssss.catalogo.domain.validation.Error
import com.lukinhasssss.catalogo.domain.validation.handler.Notification

class SaveCastMemberUseCase(
    private val castMemberGateway: CastMemberGateway
) : UseCase<CastMember?, CastMember>() {

    override fun execute(input: CastMember?): CastMember = with(input) {
        if (this == null) {
            throw NotificationException.with(anError = Error("A member cannot be null"))
        }

        val notification = Notification.create()
        validate(notification)

        if (notification.hasError()) {
            throw NotificationException.with(message = "Invalid cast member", notification = notification)
        }

        castMemberGateway.save(this)
    }
}
