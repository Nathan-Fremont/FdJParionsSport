package com.nathanfremont.domain

import com.nathanfremont.domain.repository.ISportsRepository
import com.nathanfremont.domain.repository.League
import com.nathanfremont.domain.usecases.GetAllLeaguesUseCase
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GetAllLeaguesUseCaseTest {
    @MockK
    lateinit var sportsRepository: ISportsRepository

    private lateinit var getAllLeagueUseCase: GetAllLeaguesUseCase

    @BeforeEach
    fun setUp() {
        getAllLeagueUseCase = GetAllLeaguesUseCase(
            sportsRepository = sportsRepository,
        )
    }

    @Test
    fun `getAllLeagues - when one league is returned - returns list of one item`() =
        runTest {
            val givenList: List<League> = listOf(
                League(
                    idLeague = "idLeague",
                    leagueName = "leagueName",
                )
            )
            val givenFlow = flowOf<List<League>>(
                givenList
            )
            coEvery {
                sportsRepository.getAllLeagues()
            } returns givenFlow

            val result: List<League> = getAllLeagueUseCase
                .getAllLeagues()
            assert(result == givenList)
        }
}