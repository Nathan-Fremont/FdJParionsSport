package com.nathanfremont.domain

import com.nathanfremont.domain.repository.ISportsRepository
import com.nathanfremont.domain.repository.League
import com.nathanfremont.domain.repository.Team
import com.nathanfremont.domain.usecases.GetAllLeaguesUseCase
import com.nathanfremont.domain.usecases.GetTeamsInLeagueUseCase
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GetTeamsInLeagueUseCaseTest {
    @MockK
    lateinit var sportsRepository: ISportsRepository

    private lateinit var getTeamsInLeagueUseCase: GetTeamsInLeagueUseCase

    @BeforeEach
    fun setUp() {
        getTeamsInLeagueUseCase = GetTeamsInLeagueUseCase(
            sportsRepository = sportsRepository,
        )
    }

    @Test
    fun `getTeamsInLeague - when one team is found - returns list of one item`() =
        runTest {
            val givenList: List<Team> = listOf(
                Team(
                    idTeam = "idTeam",
                    teamName = "teamName",
                    teamBadge = "teamBadge",
                )
            )
            val givenFlow = flowOf<List<Team>>(
                givenList
            )
            coEvery {
                sportsRepository.getTeamsInLeague(league = "something")
            } returns givenFlow

            val result: List<Team> = getTeamsInLeagueUseCase
                .getTeamsInLeague(league = "something")
            assert(result == givenList)
        }
}