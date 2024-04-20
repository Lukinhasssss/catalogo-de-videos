package com.lukinhasssss.catalogo.domain

import com.lukinhasssss.catalogo.domain.castmember.CastMember
import com.lukinhasssss.catalogo.domain.castmember.CastMemberType
import com.lukinhasssss.catalogo.domain.castmember.CastMemberType.ACTOR
import com.lukinhasssss.catalogo.domain.castmember.CastMemberType.DIRECTOR
import com.lukinhasssss.catalogo.domain.category.Category
import com.lukinhasssss.catalogo.domain.genre.Genre
import com.lukinhasssss.catalogo.domain.utils.IdUtils
import com.lukinhasssss.catalogo.domain.utils.InstantUtils
import net.datafaker.Faker

object Fixture {

    private val FAKER = Faker()

    fun name(): String = FAKER.name().fullName()

    object Categories {
        val aulas = Category.with(IdUtils.uuid(), "Aulas", "Conteudo gravado", true, InstantUtils.now(), InstantUtils.now())
        val talks = Category.with(IdUtils.uuid(), "Talks", "Conteudo ao vivo", false, InstantUtils.now(), InstantUtils.now(), InstantUtils.now())
        val lives = Category.with(IdUtils.uuid(), "Lives", "Conteudo ao vivo", true, InstantUtils.now(), InstantUtils.now())
    }

    object CastMembers {
        fun type(): Array<CastMemberType> = FAKER.options().option(CastMemberType.entries.toTypedArray())

        fun luffy() = CastMember.with(IdUtils.uuid(), "Monkey D Luffy", ACTOR, InstantUtils.now(), InstantUtils.now())
        fun zoro() = CastMember.with(IdUtils.uuid(), "Roronoa Zoro", ACTOR, InstantUtils.now(), InstantUtils.now())
        fun nami() = CastMember.with(IdUtils.uuid(), "Nami", DIRECTOR, InstantUtils.now(), InstantUtils.now())
    }

    object Genres {
        fun tech() = Genre.with(IdUtils.uuid(), "Technology", true, setOf(), InstantUtils.now(), InstantUtils.now())
        fun business() = Genre.with(IdUtils.uuid(), "Business", false, setOf(), InstantUtils.now(), InstantUtils.now(), InstantUtils.now())
    }
}
