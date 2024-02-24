package com.nathanfremont.data.repository

import app.cash.turbine.test
import com.nathanfremont.data.api.LeagueJsonResponse
import com.nathanfremont.data.api.ListOfLeaguesJsonResponse
import com.nathanfremont.data.api.ListOfTeamsInLeagueJsonResponse
import com.nathanfremont.data.api.SportsRemoteDataSource
import com.nathanfremont.data.api.TeamInLeagueJsonResponse
import com.nathanfremont.domain.repository.League
import com.nathanfremont.domain.repository.Team
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class SportsRepositoryTest {
    @MockK
    lateinit var sportsRemoteDataSource: SportsRemoteDataSource

    private lateinit var sportsRepository: SportsRepository

    @BeforeEach
    fun setUp() {
        sportsRepository = SportsRepository(
            sportsRemoteDataSource = sportsRemoteDataSource,
        )
    }

    @Test
    fun `getAllLeagues - when one league is given - returns list of one item and complete`() =
        runTest {
            val givenJson = ListOfLeaguesJsonResponse(
                leagues = listOf(
                    LeagueJsonResponse(
                        idLeague = "idLeague",
                        leagueName = "leagueName",
                    )
                ),
            )
            val givenFlow = flowOf<ListOfLeaguesJsonResponse>(
                givenJson
            )
            coEvery {
                sportsRemoteDataSource.getAllLeagues()
            } returns givenFlow

            val expectedEntity = listOf(
                League(
                    idLeague = "idLeague",
                    leagueName = "leagueName",
                )
            )
            val resultFlow = sportsRepository.getAllLeagues()
            resultFlow.test {
                assert(
                    awaitItem() == expectedEntity
                )
                awaitComplete()
            }
        }

    @Test
    fun `getTeamsInLeague - when one team is given - returns list of one item and complete`() =
        runTest {
            val givenJson = ListOfTeamsInLeagueJsonResponse(
                teams = listOf(
                    TeamInLeagueJsonResponse(
                        idTeam = "idTeam",
                        teamName = "teamName",
                        teamBadge = "teamBadge",
                    )
                )
            )
            val givenFlow = flowOf<ListOfTeamsInLeagueJsonResponse>(
                givenJson
            )
            coEvery {
                sportsRemoteDataSource.getTeamsInLeague(
                    league = "something",
                )
            } returns givenFlow

            val expectedEntity = listOf(
                Team(
                    idTeam = "idTeam",
                    teamName = "teamName",
                    teamBadge = "teamBadge",
                )
            )
            val resultFlow: Flow<List<Team>> = sportsRepository.getTeamsInLeague(
                league = "something",
            )
            resultFlow.test {
                assert(
                    awaitItem() == expectedEntity
                )
                awaitComplete()
            }
        }
}