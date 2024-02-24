package com.nathanfremont.data.api

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ListOfTeamsInLeagueJsonResponse(
    @SerializedName("teams")
    val teams: List<TeamInLeagueJsonResponse>,
)

@Keep
data class TeamInLeagueJsonResponse(
    @SerializedName("idTeam")
    val idTeam: String,
    @SerializedName("strTeam")
    val teamName: String,
    @SerializedName("strTeamBadge")
    val teamBadge: String,
)