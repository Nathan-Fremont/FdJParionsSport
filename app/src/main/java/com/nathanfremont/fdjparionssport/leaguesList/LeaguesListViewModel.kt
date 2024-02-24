package com.nathanfremont.fdjparionssport.leaguesList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nathanfremont.domain.repository.League
import com.nathanfremont.domain.repository.Team
import com.nathanfremont.domain.usecases.GetAllLeaguesUseCase
import com.nathanfremont.domain.usecases.GetTeamsInLeagueUseCase
import com.nathanfremont.fdjparionssport.IDispatchersProvider
import com.nathanfremont.fdjparionssport.R
import com.nathanfremont.fdjparionssport.common.CustomError
import com.nathanfremont.fdjparionssport.common.NativeText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LeaguesListViewModel @Inject constructor(
    private val getAllLeaguesUseCase: GetAllLeaguesUseCase,
    private val getTeamsInLeagueUseCase: GetTeamsInLeagueUseCase,
    private val dispatchersProvider: IDispatchersProvider,
) : ViewModel() {

    private var mutableIsLoading = MutableStateFlow(true)
    private var mutableError = MutableStateFlow<CustomError?>(null)
    private var mutableSelectedLeague = MutableStateFlow<League?>(null)
    private var mutableLeagues = MutableStateFlow(emptyList<League>())
    private var mutableTeams = MutableStateFlow(emptyList<Team>())
    private var mutableSearchValue = MutableStateFlow("")

    val state = combine(
        mutableIsLoading,
        mutableError,
        mutableLeagues,
        mutableTeams,
        mutableSearchValue,
    ) {
            isLoading: Boolean,
            error: CustomError?,
            leagues: List<League>,
            teams: List<Team>,
            searchValue: String,
        ->
        val autoCompleteOptions: List<String>
        if (searchValue.isBlank()) {
            autoCompleteOptions = emptyList()
        } else {
            val leaguesToShow: List<League> = leagues.filter { league: League ->
                league.leagueName.startsWith(
                    prefix = searchValue,
                    ignoreCase = true,
                )
            }
            autoCompleteOptions = leaguesToShow
                .take(3)
                .map { league: League ->
                    league.leagueName
                }

            selectLeague(
                league = leaguesToShow.singleOrNull(),
            )
        }
        LeaguesListState(
            isLoading = isLoading,
            error = error,
            teams = teams
                .sortedByDescending { team ->
                    team.teamName
                }
                .windowed(
                    size = 1,
                    step = 2,
                )
                .flatten(),
            autoCompleteOptions = autoCompleteOptions,
        )
    }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = LeaguesListState(
                isLoading = true,
                error = null,
                teams = emptyList(),
                autoCompleteOptions = emptyList(),
            )
        )

    init {
        getAllLeagues()
    }

    fun searchLeague(searchValue: String) {
        Timber.d("searchLeague ${"searchValue" to searchValue}")
        mutableSearchValue.value = searchValue
    }

    private fun selectLeague(league: League?) {
        Timber.d("selectLeague ${"league" to league}")
        if (mutableSelectedLeague.value != league) {
            mutableSelectedLeague.value = league
            getTeamsForLeague()
        }
    }

    private fun getTeamsForLeague() {
        Timber.d("getTeamsForLeague ${"mutableSelectedLeague.value" to mutableSelectedLeague.value}")
        mutableTeams.value = emptyList()
        val league: League = mutableSelectedLeague.value ?: return
        viewModelScope.launch(dispatchersProvider.io) {
            mutableIsLoading.value = true
            runCatching {
                val teamsInLeague: List<Team> =
                    getTeamsInLeagueUseCase.getTeamsInLeague(
                        league = league.leagueName,
                    )
                Timber.d("getTeamsForLeague ${"teamsInLeague" to teamsInLeague}")
                mutableTeams.value += teamsInLeague
            }.getOrElse { error ->
                mutableError.value = CustomError(
                    errorTitle = NativeText.Resource(R.string.error_general_message_try_again),
                )
            }
            mutableIsLoading.value = false
        }
    }

    @Synchronized
    private fun getAllLeagues() {
        Timber.d("getAllLeagues")
        viewModelScope.launch(dispatchersProvider.io) {
            mutableIsLoading.value = true
            runCatching {
                val allLeagues: List<League> =
                    getAllLeaguesUseCase.getAllLeagues()
                Timber.d("getAllLeagues ${"allLeagues" to allLeagues}")
                mutableLeagues.value += allLeagues
            }.getOrElse { error ->
                mutableError.value = CustomError(
                    errorTitle = NativeText.Resource(R.string.error_general_message_try_again),
                )
            }
            mutableIsLoading.value = false
        }
    }
}

data class LeaguesListState(
    val isLoading: Boolean,
    val error: CustomError?,
    val teams: List<Team>,
    val autoCompleteOptions: List<String>,
)