package com.nathanfremont.domain.repository

import kotlinx.coroutines.flow.Flow

interface ISportsRepository {
    fun getAllLeagues(
    ): Flow<List<League>>

    fun getTeamsInLeague(
        league: String,
    ): Flow<List<Team>>
}