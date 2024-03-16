package com.lukinhasssss.catalogo.domain

import com.lukinhasssss.catalogo.domain.castmember.CastMember
import com.lukinhasssss.catalogo.domain.castmember.CastMemberType
import com.lukinhasssss.catalogo.domain.castmember.CastMemberType.ACTOR
import com.lukinhasssss.catalogo.domain.category.Category
import com.lukinhasssss.catalogo.domain.utils.IdUtils
import com.lukinhasssss.catalogo.domain.utils.InstantUtils
import net.datafaker.Faker

object Fixture {

    private val FAKER = Faker()

    fun name(): String = FAKER.name().fullName()

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
    object CastMembers {
        private val LUFFY = CastMember.with(IdUtils.uuid(), "Monkey D Luffy", ACTOR, InstantUtils.now(), InstantUtils.now())
        private val ZORO = CastMember.with(IdUtils.uuid(), "Roronoa Zoro", ACTOR, InstantUtils.now(), InstantUtils.now())

        fun type(): Array<CastMemberType> = FAKER.options().option(CastMemberType.entries.toTypedArray())
        fun luffy() = LUFFY
        fun zoro() = ZORO
    }
}
