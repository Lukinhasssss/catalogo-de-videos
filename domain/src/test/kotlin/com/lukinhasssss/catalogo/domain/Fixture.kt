package com.lukinhasssss.catalogo.domain

import com.lukinhasssss.catalogo.domain.castmember.CastMember
import com.lukinhasssss.catalogo.domain.castmember.CastMemberType
import com.lukinhasssss.catalogo.domain.castmember.CastMemberType.ACTOR
import com.lukinhasssss.catalogo.domain.castmember.CastMemberType.DIRECTOR
import com.lukinhasssss.catalogo.domain.category.Category
import com.lukinhasssss.catalogo.domain.genre.Genre
import com.lukinhasssss.catalogo.domain.utils.IdUtils
import com.lukinhasssss.catalogo.domain.utils.InstantUtils.now
import net.datafaker.Faker

object Fixture {

    private val FAKER = Faker()

    fun name(): String = FAKER.name().fullName()

    object Categories {
        val aulas = Category.with(IdUtils.uuid(), "Aulas", "Conteudo gravado", true, now(), now())
        val talks = Category.with(IdUtils.uuid(), "Talks", "Conteudo ao vivo", false, now(), now(), now())
        val lives = Category.with(IdUtils.uuid(), "Lives", "Conteudo ao vivo", true, now(), now())
    }

    object CastMembers {
        fun type(): Array<CastMemberType> = FAKER.options().option(CastMemberType.entries.toTypedArray())

        fun luffy() = CastMember.with(IdUtils.uuid(), "Monkey D Luffy", ACTOR, now(), now())
        fun zoro() = CastMember.with(IdUtils.uuid(), "Roronoa Zoro", ACTOR, now(), now())
        fun nami() = CastMember.with(IdUtils.uuid(), "Nami", DIRECTOR, now(), now())
    }

    object Genres {
        fun tech() = Genre.with(IdUtils.uuid(), "Technology", true, setOf("c456"), now(), now())
        fun business() = Genre.with(IdUtils.uuid(), "Business", false, setOf(), now(), now(), now())
        fun marketing() = Genre.with(IdUtils.uuid(), "Marketing", true, setOf("c123"), now(), now())
    }
}
