package com.lukinhasssss.catalogo.domain

import com.lukinhasssss.catalogo.domain.castmember.CastMember
import com.lukinhasssss.catalogo.domain.castmember.CastMemberType
import com.lukinhasssss.catalogo.domain.castmember.CastMemberType.ACTOR
import com.lukinhasssss.catalogo.domain.castmember.CastMemberType.DIRECTOR
import com.lukinhasssss.catalogo.domain.category.Category
import com.lukinhasssss.catalogo.domain.genre.Genre
import com.lukinhasssss.catalogo.domain.utils.IdUtils
import com.lukinhasssss.catalogo.domain.utils.InstantUtils.now
import com.lukinhasssss.catalogo.domain.video.Rating
import com.lukinhasssss.catalogo.domain.video.Video
import net.datafaker.Faker
import java.time.Year

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

    object Videos {
        fun systemDesign() = Video.with(
            id = IdUtils.uuid(),
            title = "System Design",
            description = "System Design description",
            launchedAt = Year.now().value,
            duration = 60.0,
            rating = Rating.L.name,
            opened = true,
            published = true,
            createdAt = now().toString(),
            updatedAt = now().toString(),
            banner = "https://banner.com",
            thumbnail = "https://thumbnail.com",
            thumbnailHalf = "https://thumbnail-half.com",
            trailer = "https://trailer.com",
            video = "https://video.com",
            categories = setOf(IdUtils.uuid()),
            genres = setOf(IdUtils.uuid()),
            castMembers = setOf(IdUtils.uuid())
        )

        fun cleanCode() = Video.with(
            id = IdUtils.uuid(),
            title = "Clean Code",
            description = "Clean Code description",
            launchedAt = Year.now().value,
            duration = 60.0,
            rating = Rating.L.name,
            opened = true,
            published = true,
            createdAt = now().toString(),
            updatedAt = now().toString(),
            banner = "https://banner.com",
            thumbnail = "https://thumbnail.com",
            thumbnailHalf = "https://thumbnail-half.com",
            trailer = "https://trailer.com",
            video = "https://video.com",
            categories = setOf(IdUtils.uuid()),
            genres = setOf(IdUtils.uuid()),
            castMembers = setOf(IdUtils.uuid())
        )
    }
}
