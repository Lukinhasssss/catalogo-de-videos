package com.lukinhasssss.catalogo.domain

import net.datafaker.Faker

object Fixture {

    private val FAKER = Faker()

    fun name() = FAKER.name().fullName()
    fun year() = FAKER.random().nextInt(2010, 2030)
    fun title() = FAKER.oscarMovie().movieName()
    fun bool() = FAKER.bool().bool()
    fun duration() = FAKER.options().option(listOf(120.0, 15.5, 35.5, 10.0, 2.0))
}
