package com.nathanfremont.domain.usecases

import com.nathanfremont.common.CustomTimeoutException
import com.nathanfremont.common.withTimeout
import com.nathanfremont.domain.repository.ISportsRepository
import com.nathanfremont.domain.repository.Team
import javax.inject.Inject

class GetTeamsInLeagueUseCase @Inject constructor(
    private val sportsRepository: ISportsRepository,
) {
    @Throws(CustomTimeoutException::class)
    suspend fun getTeamsInLeague(
        league: String,
    ): List<Team> = sportsRepository
        .getTeamsInLeague(
            league = league,
        )
        .withTimeout(5_000L)
}