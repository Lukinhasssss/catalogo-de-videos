package com.lukinhasssss.catalogo.infrastructure.authentication

fun interface GetClientCredentials {
    fun retrieve(): String
}
