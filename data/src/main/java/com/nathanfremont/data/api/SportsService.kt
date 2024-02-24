package com.nathanfremont.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface SportsService {

    @GET("{apiKey}/all_leagues.php")
    suspend fun getAllLeagues(
        @Path("apiKey")
        apiKey: String,
    ): Response<ListOfLeaguesJsonResponse>

    @GET("{apiKey}/search_all_teams.php")
    suspend fun getTeamsInLeague(
        @Path("apiKey")
        apiKey: String,
        @Query("l")
        league: String,
    ): Response<ListOfTeamsInLeagueJsonResponse>
}