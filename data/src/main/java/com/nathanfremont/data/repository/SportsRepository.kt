package com.nathanfremont.data.repository

import com.nathanfremont.data.api.LeagueJsonResponse
import com.nathanfremont.data.api.ListOfLeaguesJsonResponse
import com.nathanfremont.data.api.ListOfTeamsInLeagueJsonResponse
import com.nathanfremont.data.api.SportsRemoteDataSource
import com.nathanfremont.data.api.TeamInLeagueJsonResponse
import com.nathanfremont.domain.repository.ISportsRepository
import com.nathanfremont.domain.repository.League
import com.nathanfremont.domain.repository.Team
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SportsRepository @Inject constructor(
    private val sportsRemoteDataSource: SportsRemoteDataSource,
) : ISportsRepository {
    override fun getAllLeagues(
    ): Flow<List<League>> = sportsRemoteDataSource
        .getAllLeagues()
        .map { value: ListOfLeaguesJsonResponse ->
            value.leagues.map { jsonLeague: LeagueJsonResponse ->
                League(
                    idLeague = jsonLeague.idLeague,
                    leagueName = jsonLeague.leagueName,
                )
            }
        }

    override fun getTeamsInLeague(
        league: String,
    ): Flow<List<Team>> = sportsRemoteDataSource
        .getTeamsInLeague(
            league = league,
        )
        .map { value: ListOfTeamsInLeagueJsonResponse ->
            value.teams.map { jsonTeam: TeamInLeagueJsonResponse ->
                Team(
                    idTeam = jsonTeam.idTeam,
                    teamName = jsonTeam.teamName,
                    teamBadge = jsonTeam.teamBadge,
                )
            }
        }
}