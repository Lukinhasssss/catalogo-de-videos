package com.lukinhasssss.catalogo.domain

import com.lukinhasssss.catalogo.domain.category.Category
import com.lukinhasssss.catalogo.domain.utils.IdUtils
import com.lukinhasssss.catalogo.domain.utils.InstantUtils
import net.datafaker.Faker

object Fixture {

    private val FAKER = Faker()

    fun name(): String = FAKER.name().fullName()
    fun year() = FAKER.random().nextInt(2010, 2030)
    fun title() = FAKER.oscarMovie().movieName()
    fun description() = FAKER.movie().quote()
    fun bool() = FAKER.bool().bool()
    fun duration() = FAKER.options().option(listOf(120.0, 15.5, 35.5, 10.0, 2.0))

    object Categories {
        val aulas = Category.with(
            anId = IdUtils.uuid(),
            aName = "Aulas",
            aDescription = "Conteudo gravado",
            isActive = true,
            createdAt = InstantUtils.now(),
            updatedAt = InstantUtils.now()
        )

        val talks = Category.with(
            anId = IdUtils.uuid(),
            aName = "Talks",
            aDescription = "Conteudo ao vivo",
            isActive = false,
            createdAt = InstantUtils.now(),
            updatedAt = InstantUtils.now(),
            deletedAt = InstantUtils.now()
        )

        val lives = Category.with(
            anId = IdUtils.uuid(),
            aName = "Lives",
            aDescription = "Conteudo ao vivo",
            isActive = true,
            createdAt = InstantUtils.now(),
            updatedAt = InstantUtils.now()
        )
    }
}
