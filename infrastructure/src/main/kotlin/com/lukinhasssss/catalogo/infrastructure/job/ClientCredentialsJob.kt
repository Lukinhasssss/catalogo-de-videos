package com.lukinhasssss.catalogo.infrastructure.job

import com.lukinhasssss.catalogo.infrastructure.authentication.RefreshClientCredentials
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class ClientCredentialsJob(
    private val refreshClientCredentials: RefreshClientCredentials
) {

    companion object {
        private const val FIXED_RATE = 3L
        private const val INITIAL_DELAY = 3L
    }

    @Scheduled(fixedRate = FIXED_RATE, timeUnit = TimeUnit.MINUTES, initialDelay = INITIAL_DELAY)
    fun refreshClientCredentials() = refreshClientCredentials.refresh()
}
