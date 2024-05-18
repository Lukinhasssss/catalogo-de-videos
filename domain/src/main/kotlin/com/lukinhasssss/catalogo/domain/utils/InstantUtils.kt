package com.lukinhasssss.catalogo.domain.utils

import java.time.Instant
import java.time.temporal.ChronoUnit

object InstantUtils {
    private const val UNIX_PRECISION = 1_000L

    fun now(): Instant = Instant.now().truncatedTo(ChronoUnit.MILLIS)

    fun fromTimestamp(timestamp: Long): Instant = Instant.ofEpochMilli(timestamp / UNIX_PRECISION)
}
