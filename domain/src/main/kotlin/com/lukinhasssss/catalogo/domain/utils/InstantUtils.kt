package com.lukinhasssss.catalogo.domain.utils

import java.time.Instant
import java.time.temporal.ChronoUnit

object InstantUtils {
    fun now() = Instant.now().truncatedTo(ChronoUnit.MILLIS)
}
