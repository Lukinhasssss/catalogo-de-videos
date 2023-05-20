package com.lukinhasssss.catalogo.domain

import io.github.serpro69.kfaker.Faker

class Fixture {

    companion object {
        private val FAKER = Faker()

        fun name() = FAKER.onePiece.characters()
        fun year() = FAKER.random.nextInt(2010, 2030)
        fun title() = FAKER.movie.title()
        fun bool() = FAKER.random.nextBoolean()
        fun duration() = FAKER.random.randomValue(listOf(120.0, 15.5, 35.5, 10.0, 2.0))
    }
}
