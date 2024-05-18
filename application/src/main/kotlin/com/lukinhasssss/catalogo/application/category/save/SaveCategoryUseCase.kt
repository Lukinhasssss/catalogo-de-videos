package com.lukinhasssss.catalogo.application.category.save

import com.lukinhasssss.catalogo.application.UseCase
import com.lukinhasssss.catalogo.domain.category.Category
import com.lukinhasssss.catalogo.domain.category.CategoryGateway
import com.lukinhasssss.catalogo.domain.exception.NotificationException
import com.lukinhasssss.catalogo.domain.validation.Error
import com.lukinhasssss.catalogo.domain.validation.handler.Notification

class SaveCategoryUseCase(
    private val categoryGateway: CategoryGateway
) : UseCase<Category?, Category>() {

    override fun execute(input: Category?): Category = with(input) {
        if (this == null) {
            throw NotificationException.with(anError = Error("A category cannot be null"))
        }

        val notification = Notification.create()
        validate(notification)

        if (notification.hasError()) {
            throw NotificationException.with(message = "Invalid category", notification = notification)
        }

        categoryGateway.save(this)
    }
}
