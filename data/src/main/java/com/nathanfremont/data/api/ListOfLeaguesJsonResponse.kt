package com.nathanfremont.data.api

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ListOfLeaguesJsonResponse(
    @SerializedName("leagues")
    val leagues: List<LeagueJsonResponse>,
)

@Keep
data class LeagueJsonResponse(
    @SerializedName("idLeague")
    val idLeague: String,
    @SerializedName("strLeague")
    val leagueName: String,
)