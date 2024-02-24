package com.nathanfremont.data.api

import com.google.gson.Gson
import com.nathanfremont.common.logd
import com.nathanfremont.common.retryOnInternalErrorServerHttpCodes
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.filterNotNull
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Inject
import javax.inject.Named

class SportsRemoteDataSource @Inject constructor(
    @Named("baseApiUrl")
    baseApiUrl: String,
    @Named("apiKey")
    private val apiKey: String,
    baseOkHttpClient: OkHttpClient,
) {
    private val service: SportsService = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(Gson()))
        .baseUrl(baseApiUrl)
        .client(baseOkHttpClient)
        .build()
        .create(SportsService::class.java)

    fun getAllLeagues(): Flow<ListOfLeaguesJsonResponse> = callbackFlow {
        val moviesForPage = service.getAllLeagues(
            apiKey = apiKey,
        )
        trySend(moviesForPage)
        close()
        awaitClose()
    }
        .retryOnInternalErrorServerHttpCodes()
        .logd(
            tag = "SportsRemoteDataSource",
            method = "getAllLeagues",
        )
        .filterNotNull()

    fun getTeamsInLeague(
        league: String,
    ): Flow<ListOfTeamsInLeagueJsonResponse> = callbackFlow {
        val moviesForPage = service.getTeamsInLeague(
            apiKey = apiKey,
            league = league,
        )
        trySend(moviesForPage)
        close()
        awaitClose()
    }
        .retryOnInternalErrorServerHttpCodes()
        .logd(
            tag = "SportsRemoteDataSource",
            method = "getTeamsInLeague",
            "league" to league,
        )
        .filterNotNull()
}