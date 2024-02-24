package com.nathanfremont.domain.usecases

import com.nathanfremont.common.CustomTimeoutException
import com.nathanfremont.common.withTimeout
import com.nathanfremont.domain.repository.ISportsRepository
import com.nathanfremont.domain.repository.League
import javax.inject.Inject

class GetAllLeaguesUseCase @Inject constructor(
    private val sportsRepository: ISportsRepository,
) {
    @Throws(CustomTimeoutException::class)
    suspend fun getAllLeagues(
    ): List<League> = sportsRepository
        .getAllLeagues()
        .withTimeout(5_000L)
}