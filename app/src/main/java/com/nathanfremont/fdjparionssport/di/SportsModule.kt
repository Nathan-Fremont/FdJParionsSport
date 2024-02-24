package com.nathanfremont.fdjparionssport.di

import com.nathanfremont.data.repository.SportsRepository
import com.nathanfremont.domain.repository.ISportsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class SportsModule {
    @Provides
    @Singleton
    fun provideSportsRepository(
        sportsRepository: SportsRepository,
    ): ISportsRepository = sportsRepository
}